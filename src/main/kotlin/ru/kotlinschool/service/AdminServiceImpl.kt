package ru.kotlinschool.service

import org.springframework.beans.factory.annotation.Autowired
import ru.kotlinschool.dto.BillData
import ru.kotlinschool.dto.BillServiceData
import org.springframework.stereotype.Service
import ru.kotlinschool.dto.HouseDto
import ru.kotlinschool.dto.PublicServiceDto
import ru.kotlinschool.dto.UserDto
import ru.kotlinschool.exception.EntityNotFoundException
import ru.kotlinschool.persistent.entity.Bill
import ru.kotlinschool.persistent.entity.CalculationType
import ru.kotlinschool.persistent.entity.House
import ru.kotlinschool.persistent.entity.ManagementCompany
import ru.kotlinschool.persistent.entity.Metric
import ru.kotlinschool.persistent.entity.PublicService
import ru.kotlinschool.persistent.entity.Rate
import ru.kotlinschool.persistent.repository.BillRepository
import ru.kotlinschool.persistent.repository.HouseRepository
import ru.kotlinschool.persistent.repository.ManagementCompanyRepository
import ru.kotlinschool.persistent.repository.PublicServiceRepository
import ru.kotlinschool.persistent.repository.RateRepository
import ru.kotlinschool.util.ExcelService
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.stream.Collectors

@Service
class AdminServiceImpl @Autowired constructor(
    private val managementCompanyRep: ManagementCompanyRepository,
    private val houseRep: HouseRepository,
    private val rateRep: RateRepository,
    private val publicServiceRep: PublicServiceRepository,
    private val billRep: BillRepository,
    private val excelService: ExcelService
) : AdminService {

    /**
     * Проверка, что пользователь является админом
     */
    override fun isAdmin(userId: Long): Boolean {
        return managementCompanyRep.findByUserId(userId) != null
    }

    /**
     * Добавление УК
     */
    override fun registerManagementCompany(adminId: Long, name: String, inn: String) {
        managementCompanyRep.save(ManagementCompany(name, inn, adminId))
    }

    /**
     * Возвращает все дома УК(одному администратору соответствует одна УК)
     */
    override fun getHouses(adminId: Long): List<HouseDto> {
        return houseRep.findHousesByAdminId(adminId).map { HouseDto(it.id, it.address) }
    }

    /**
     * Добавление дома(одному администратору соответствует одна УК)
     */
    override fun registerHouse(adminId: Long, address: String) {
        val managementCompany = managementCompanyRep.findByUserId(adminId)
        managementCompany ?: throw EntityNotFoundException("Не найдена УК по ид админа = $adminId")
        houseRep.save(House(managementCompany, address))
    }

    /**
     * Добавление коммунальной услуги
     */
    override fun registerPublicService(houseId: Long, name: String, calculationType: String, unit: String) {
        val house = houseRep.findById(houseId)
            .orElseThrow { EntityNotFoundException("Не найден дом с ид = $houseId") }
        publicServiceRep.save(PublicService(house, name, CalculationType.valueOf(calculationType), unit))
    }

    /**
     * Получить все услуги по дому
     */
    override fun getPublicServices(houseId: Long): List<PublicServiceDto> {
        return houseRep.findById(houseId)
            .orElseThrow { EntityNotFoundException("Не найден дом с ид = $houseId") }
            .publicServices.map { PublicServiceDto(it.id, it.name, it.calculationType) }
    }

    /**
     * Внесение тарифа для услуги
     */
    override fun setRate(publicServiceId: Long, value: BigDecimal) {
        val publicService = publicServiceRep.findById(publicServiceId)
            .orElseThrow { EntityNotFoundException("Не найдена коммунальная услуга с ид = $publicServiceId") }
        rateRep.save(
            Rate(
                publicService,
                value,
                LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
            )
        )
    }

    /**
     * Все собственники квартир
     */
    override fun getUsers(houseId: Long): List<UserDto>{
        return houseRep.findById(houseId)
            .orElseThrow { EntityNotFoundException("Не найден дом с ид = $houseId") }
            .flats.map { UserDto(it.userId, it.chatId) }.distinct()
    }


    /**
     * Посчитать квитанции
     */
    override fun calculateBills(houseId: Long) {
        val calculationData = LocalDate.now()
        val house = houseRep.findById(houseId)
            .orElseThrow { EntityNotFoundException("Не найден дом с ид = $houseId") }
        val rates = house.publicServices.stream()
            .collect(Collectors.toMap(PublicService::id, PublicService::rates))
            .mapValues { (_, v) ->
                v.filter {
                    it.dateBegin.isBefore(calculationData)
                            || it.dateBegin.isEqual(calculationData)
                }.maxBy { it.dateBegin }.sum
            }
        val year = calculationData.year
        val month = calculationData.monthValue
        house.flats.forEach {
            val currentMetrics: Map<PublicService, Double> = it.metrics
                .metricGrouping { m -> checkIfDateInMonth(m.actionDate, calculationData) and !m.isInit }
            val previousMetrics: Map<PublicService, Double> = it.metrics
                .metricGrouping { m -> checkIfDateInMonth(m.actionDate, calculationData.minusMonths(1)) }
            val initMetrics: Map<PublicService, Double> = it.metrics
                .metricGrouping { m -> checkIfDateInMonth(m.actionDate, calculationData) and m.isInit }
            val param = BillData(
                year,
                month,
                house.managementCompany.name,
                "${house.address}, кв. ${it.number}",
                it.area,
                it.numberOfResidents,
                house.publicServices.map { serv ->
                    BillServiceData(
                        serv.name, serv.unit, serv.calculationType, rates[serv.id]!!, currentMetrics[serv],
                        previousMetrics[serv] ?: initMetrics[serv]
                    )
                }
            )
            //Расчитываем квитанцию
            var content = excelService.build(param)
            billRep.save(Bill(it, year, month, content))
        }
    }

    private fun List<Metric>.metricGrouping(predicate: (Metric) -> Boolean): Map<PublicService, Double> {
        return this.filter(predicate)
            .groupBy(Metric::publicService).mapValues { (_, v) ->
            v.maxBy(Metric::actionDate).value
        }
    }

    private fun checkIfDateInMonth(inDate: LocalDate, dateOfMonth: LocalDate): Boolean {
        val firstDayOfMonth = dateOfMonth.with(TemporalAdjusters.firstDayOfMonth())
        val lastDayOfMonth = dateOfMonth.with(TemporalAdjusters.lastDayOfMonth())
        return (inDate.isAfter(firstDayOfMonth) || inDate.isEqual(firstDayOfMonth))
                && (inDate.isBefore(lastDayOfMonth) || inDate.isEqual(lastDayOfMonth))
    }
}
