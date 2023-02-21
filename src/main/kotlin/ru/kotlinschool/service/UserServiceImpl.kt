package ru.kotlinschool.service

import org.springframework.stereotype.Service
import ru.kotlinschool.data.FlatData
import ru.kotlinschool.data.HouseData
import ru.kotlinschool.data.ManagementCompanyData
import ru.kotlinschool.data.PublicServiceData
import ru.kotlinschool.exception.EntityNotFoundException
import ru.kotlinschool.exception.TooManyMetricAdditionsException
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
    override fun getManagementCompanies(): List<ManagementCompanyData> {
        return managementCompanyRep.findAll().map { ManagementCompanyData(it.id, it.name) }
    }

    /**
     * Возвращает все дома УК
     */
    override fun getHouses(managementCompanyId: Long): List<HouseData> {
        return houseRep.findHousesByManagementCompany(managementCompanyId).map { HouseData(it.id, it.address) }
    }

    /**
     * Добавление квартиры
     */
    override fun registerFlat(
        userId: Long,
        chatId: Long,
        houseId: Long,
        flatNumber: String,
        area: Double,
        numberOfResidents: Long
    ): FlatData {
        val house = houseRep.findById(houseId).orElseThrow { EntityNotFoundException("Не найден дом с ид = $houseId") }
        return flatRep.save(Flat(userId, chatId, house, flatNumber, area, numberOfResidents))
            .let { FlatData(it.id, "${it.house.address}, д. ${it.number}") }
    }

    /**
     * Получить все квартиры пользователя
     */
    override fun getFlats(userId: Long): List<FlatData> {
        return flatRep.findByUserId(userId).map { FlatData(it.id, "${it.house.address}, кв. ${it.number}") }
    }

    /**
     * Получить все услуги по квартире
     */
    override fun getPublicServices(flatId: Long): List<PublicServiceData> {
        return flatRep.findById(flatId)
            .orElseThrow { EntityNotFoundException("Не найдена квартира с ид = $flatId") }
            .house.publicServices.map { PublicServiceData(it.id, it.name, it.calculationType) }
    }

    /**
     * Добавить показания
     */
    override fun addMetric(flatId: Long, publicServiceId: Long, value: Double, isInit: Boolean) {
        val flat = flatRep.findById(flatId)
            .orElseThrow { EntityNotFoundException("Не найдена квартира с ид = $flatId") }
        val publicService = publicServiceRep.findById(publicServiceId)
            .orElseThrow { EntityNotFoundException("Не найдена коммунальная услуга с ид = $publicServiceId") }
        runCatching {
            metricRep.save(Metric(flat, publicService, value, LocalDate.now(), isInit))
        }.getOrElse { throw TooManyMetricAdditionsException() }
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
