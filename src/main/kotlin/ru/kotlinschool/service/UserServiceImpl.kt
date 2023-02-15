package ru.kotlinschool.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kotlinschool.dto.FlatDto
import ru.kotlinschool.dto.HouseDto
import ru.kotlinschool.dto.ManagementCompanyDto
import ru.kotlinschool.dto.PublicServiceDto
import ru.kotlinschool.exception.EntityNotFoundException
import ru.kotlinschool.persistent.entity.Flat
import ru.kotlinschool.persistent.entity.Metric
import ru.kotlinschool.persistent.repository.BillRepository
import ru.kotlinschool.persistent.repository.FlatRepository
import ru.kotlinschool.persistent.repository.HouseRepository
import ru.kotlinschool.persistent.repository.ManagementCompanyRepository
import ru.kotlinschool.persistent.repository.MetricRepository
import ru.kotlinschool.persistent.repository.PublicServiceRepository
import java.time.LocalDate


@Service
class UserServiceImpl(
    @Autowired val managementCompanyRep: ManagementCompanyRepository,
    @Autowired val houseRep: HouseRepository,
    @Autowired val flatRep: FlatRepository,
    @Autowired val publicServiceRep: PublicServiceRepository,
    @Autowired val metricRep: MetricRepository,
    @Autowired val billRep: BillRepository
) : UserService {

    /**
     * Возвращает все УК
     */
    override fun getManagementCompanies(): List<ManagementCompanyDto> {
       return managementCompanyRep.findAll().map { ManagementCompanyDto(it.id, it.name) }
    }

    /**
     * Возвращает все дома УК
     */
    override fun getHouses(managementCompanyId: Long): List<HouseDto> {
        return houseRep.findHousesByManagementCompany(managementCompanyId).map { HouseDto(it.id, it.address) }
    }

    /**
     * Добавление квартиры
     */
    override fun registerFlat(userId: Long, houseId: Long, flatNumber: String, area: Double, numberOfResidents: Int) {
        val house = houseRep.findById(houseId).orElseThrow { EntityNotFoundException("Не найден дом с ид = $houseId") }
        flatRep.save(Flat(userId, house, flatNumber, area, numberOfResidents))
    }

    /**
     * Получить все квартиры пользователя
     */
    override fun getFlats(userId: Long): List<FlatDto> {
        return flatRep.findByUserId(userId).map { FlatDto(it.id, "${it.house.address}, д. ${it.number}") }
    }

    /**
     * Получить все услуги по квартире
     */
    override fun getPublicServices(flatId: Long): List<PublicServiceDto> {
        return flatRep.findById(flatId)
            .orElseThrow { EntityNotFoundException("Не найдена квартира с ид = $flatId") }
            .house.publicServices.map { PublicServiceDto(it.id, it.name) }
    }

    /**
     * Добавить показания
     */
    override fun addMetric(flatId: Long, publicServiceId: Long, value: Double) {
        val flat = flatRep.findById(flatId)
            .orElseThrow { EntityNotFoundException("Не найдена квартира с ид = $flatId") }
        val publicService = publicServiceRep.findById(publicServiceId)
            .orElseThrow { EntityNotFoundException("Не найдена коммунальная услуга с ид = $publicServiceId") }
        metricRep.save(Metric(flat, publicService, value, LocalDate.now()))
    }

    /**
     * Получить квитанцию
     */
    override fun getBill(flatId: Long, month: Int): ByteArray {
        val bill = billRep.findBill(flatId, month)
        bill ?: throw EntityNotFoundException("Не найдена квитанция за $month месяц с ид = $flatId")
        return bill.billData
    }
}
