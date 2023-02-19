package ru.kotlinschool.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kotlinschool.dto.FlatDto
import ru.kotlinschool.dto.HouseDto
import ru.kotlinschool.dto.ManagementCompanyDto
import ru.kotlinschool.dto.PublicServiceDto
import ru.kotlinschool.exception.EntityNotFoundException
import ru.kotlinschool.persistent.entity.Client
import ru.kotlinschool.persistent.entity.Flat
import ru.kotlinschool.persistent.entity.Metric
import ru.kotlinschool.persistent.repository.BillRepository
import ru.kotlinschool.persistent.repository.ClientRepository
import ru.kotlinschool.persistent.repository.FlatRepository
import ru.kotlinschool.persistent.repository.HouseRepository
import ru.kotlinschool.persistent.repository.ManagementCompanyRepository
import ru.kotlinschool.persistent.repository.MetricRepository
import ru.kotlinschool.persistent.repository.PublicServiceRepository
import java.time.LocalDate


@Service
class UserServiceImpl @Autowired constructor(
    private val managementCompanyRep: ManagementCompanyRepository,
    private val houseRep: HouseRepository,
    private val flatRep: FlatRepository,
    private val publicServiceRep: PublicServiceRepository,
    private val metricRep: MetricRepository,
    private val billRep: BillRepository
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
    override fun registerFlat(userId: Long,
                              chatId: Long,
                              houseId: Long,
                              flatNumber: String,
                              area: Double,
                              numberOfResidents: Long) {
        val house = houseRep.findById(houseId).orElseThrow { EntityNotFoundException("Не найден дом с ид = $houseId") }
        flatRep.save(Flat(userId, chatId, house, flatNumber, area, numberOfResidents))
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
            .house.publicServices.map { PublicServiceDto(it.id, it.name, it.calculationType) }
    }

    /**
     * Добавить показания
     */
    override fun addMetric(flatId: Long, publicServiceId: Long, value: Double, isInit: Boolean) {
        val flat = flatRep.findById(flatId)
            .orElseThrow { EntityNotFoundException("Не найдена квартира с ид = $flatId") }
        val publicService = publicServiceRep.findById(publicServiceId)
            .orElseThrow { EntityNotFoundException("Не найдена коммунальная услуга с ид = $publicServiceId") }
        metricRep.save(Metric(flat, publicService, value, LocalDate.now(), isInit))
    }

    /**
     * Получить квитанцию
     */
    override fun getBill(flatId: Long, year: Int, month: Int): ByteArray {
        val bill = billRep.findBill(flatId, year, month)
        bill ?: throw EntityNotFoundException("Не найдена квитанция за $month месяц с ид = $flatId")
        return bill.billData
    }
}
