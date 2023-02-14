package ru.kotlinschool.service

import org.springframework.beans.factory.annotation.Autowired
import ru.kotlinschool.dto.HouseDto
import ru.kotlinschool.dto.PublicServiceDto
import ru.kotlinschool.exception.EntityNotFoundException
import ru.kotlinschool.persistent.entity.CalculationType
import ru.kotlinschool.persistent.entity.House
import ru.kotlinschool.persistent.entity.ManagementCompany
import ru.kotlinschool.persistent.entity.PublicService
import ru.kotlinschool.persistent.entity.Rate
import ru.kotlinschool.persistent.repository.BillRepository
import ru.kotlinschool.persistent.repository.HouseRepository
import ru.kotlinschool.persistent.repository.ManagementCompanyRepository
import ru.kotlinschool.persistent.repository.MetricRepository
import ru.kotlinschool.persistent.repository.PublicServiceRepository
import ru.kotlinschool.persistent.repository.RateRepository
import java.math.BigDecimal
import java.time.LocalDate

class AdminServiceImpl(
    @Autowired val managementCompanyRep: ManagementCompanyRepository,
    @Autowired val houseRep: HouseRepository,
    @Autowired val rateRep: RateRepository,
    @Autowired val publicServiceRep: PublicServiceRepository,
    @Autowired val metricRep: MetricRepository,
    @Autowired val billRep: BillRepository
) : AdminService {

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
    override fun registerPublicService(houseId: Long, name: String, calculationType: String) {
        val house = houseRep.findById(houseId)
            .orElseThrow { EntityNotFoundException("Не найден дом с ид = $houseId") }
        publicServiceRep.save(PublicService(house, name, CalculationType.valueOf(calculationType)))
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
    override fun setRate(publicServiceId: Long, value: BigDecimal, dateBegin: LocalDate) {
        val publicService = publicServiceRep.findById(publicServiceId)
            .orElseThrow { EntityNotFoundException("Не найдена коммунальная услуга с ид = $publicServiceId") }
        rateRep.save(Rate(publicService, value, dateBegin))
    }

    /**
     * Посчитать квитанции
     */
    override fun calculateBills(houseId: Long) {
        TODO("Not yet implemented")
    }

}
