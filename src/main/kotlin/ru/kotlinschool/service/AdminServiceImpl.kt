package ru.kotlinschool.service

import org.springframework.beans.factory.annotation.Autowired
import ru.kotlinschool.dto.BillData
import ru.kotlinschool.dto.BillServiceData
import ru.kotlinschool.dto.HouseDto
import ru.kotlinschool.dto.PublicServiceDto
import ru.kotlinschool.dto.UserDto
import ru.kotlinschool.exception.EntityNotFoundException
import ru.kotlinschool.persistent.entity.Bill
import ru.kotlinschool.persistent.entity.CalculationType
import ru.kotlinschool.persistent.entity.House
import ru.kotlinschool.persistent.entity.ManagementCompany
import ru.kotlinschool.persistent.entity.PublicService
import ru.kotlinschool.persistent.entity.Rate
import ru.kotlinschool.persistent.repository.BillRepository
import ru.kotlinschool.persistent.repository.HouseRepository
import ru.kotlinschool.persistent.repository.ManagementCompanyRepository
import ru.kotlinschool.persistent.repository.PublicServiceRepository
import ru.kotlinschool.persistent.repository.RateRepository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.stream.Collectors

class AdminServiceImpl @Autowired constructor(
    private val managementCompanyRep: ManagementCompanyRepository,
    private val houseRep: HouseRepository,
    private val rateRep: RateRepository,
    private val publicServiceRep: PublicServiceRepository,
    private val billRep: BillRepository
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
            .publicServices.map { PublicServiceDto(it.id, it.name) }
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
                LocalDate.now().plusMonths(1).with(TemporalAdjusters.firstDayOfMonth())
            )
        )
    }

    /**
     * Все собственники квартир
     */
    override fun getUsers(houseId: Long, userId: Long): List<UserDto>{
        return houseRep.findById(houseId)
            .orElseThrow { EntityNotFoundException("Не найден дом с ид = $houseId") }
            .flats.map { UserDto(it.userId, it.chatId) }
    }


    /**
     * Посчитать квитанции
     */
    override fun calculateBills(houseId: Long) {
        val house = houseRep.findById(houseId)
            .orElseThrow { EntityNotFoundException("Не найден дом с ид = $houseId") }
        val rates = house.publicServices.stream()
            .collect(Collectors.toMap(PublicService::id, PublicService::rates))
            .mapValues { (_, v) -> v.maxByOrNull { it.dateBegin }!!.sum }
        val year = LocalDate.now().year
        val month = LocalDate.now().monthValue
        house.flats.forEach {
            val currentMetrics: Map<PublicService, Double> = it.metrics
                .filter { m -> checkDateInMonth(m.actionDate, LocalDate.now()) }
                .groupBy { p -> p.publicService }.mapValues { (_, v) -> v.maxByOrNull { m -> m.actionDate }!!.value }
            val previousMetrics: Map<PublicService, Double> = it.metrics
                .filter { m -> checkDateInMonth(m.actionDate, LocalDate.now().minusMonths(1)) }
                .groupBy { p -> p.publicService }.mapValues { (_, v) -> v.maxByOrNull { m -> m.actionDate }!!.value }
            val initMetrics: Map<PublicService, Double> = it.metrics
                .filter { m -> checkDateInMonth(m.actionDate, LocalDate.now()) and m.isInit}
                .groupBy { p -> p.publicService }.mapValues { (_, v) -> v.maxByOrNull { m -> m.actionDate }!!.value }
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
            var content: ByteArray? = byteArrayOf(10, 2, 15, 11)//вызов excelService
            billRep.save(Bill(it, year, month, content!!))
        }
    }

    private fun checkDateInMonth(inDate: LocalDate, dateOfMonth: LocalDate): Boolean {
        return (inDate.isAfter(dateOfMonth.with(TemporalAdjusters.firstDayOfMonth()))
                || inDate.isEqual(dateOfMonth.with(TemporalAdjusters.firstDayOfMonth())))
                && (inDate.isBefore(dateOfMonth.with(TemporalAdjusters.lastDayOfMonth()))
                || inDate.isEqual(dateOfMonth.with(TemporalAdjusters.lastDayOfMonth())))
    }

}
