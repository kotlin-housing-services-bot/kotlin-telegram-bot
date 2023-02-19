package ru.kotlinschool.service

import ru.kotlinschool.data.FlatData
import ru.kotlinschool.data.HouseData
import ru.kotlinschool.data.ManagementCompanyData
import ru.kotlinschool.data.PublicServiceData

interface UserService {
    /**
     * Возвращает все УК
     */
    fun getManagementCompanies(): List<ManagementCompanyData>

    /**
     * Возвращает все дома УК
     */
    fun getHouses(managementCompanyId: Long): List<HouseData>

    /**
     * Добавление квартиры
     */
    fun registerFlat(
        userId: Long,
        chatId: Long,
        houseId: Long,
        flatNumber: String,
        area: Double,
        numberOfResidents: Long
    ): FlatData

    /**
     * Получить все квартиры пользователя
     */
    fun getFlats(userId: Long): List<FlatData>

    /**
     * Получить все услуги по квартире
     */
    fun getPublicServices(flatId: Long): List<PublicServiceData>

    /**
     * Добавить показания
     */
    fun addMetric(flatId: Long, publicServiceId: Long, value: Double, isInit: Boolean = false)

    /**
     * Получить квитанцию
     */
    fun getBill(flatId: Long, year: Int, month: Int): ByteArray
}

