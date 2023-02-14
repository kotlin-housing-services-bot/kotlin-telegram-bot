package ru.kotlinschool.service

import ru.kotlinschool.dto.FlatDto
import ru.kotlinschool.dto.HouseDto
import ru.kotlinschool.dto.ManagementCompanyDto
import ru.kotlinschool.dto.PublicServiceDto

interface UserService {
    /**
     * Возвращает все УК
     */
    fun getManagementCompanies(): List<ManagementCompanyDto>

    /**
     * Возвращает все дома УК
     */
    fun getHouses(managementCompanyId: Long): List<HouseDto>

    /**
     * Добавление квартиры
     */
    fun registerFlat(userId: Long, houseId: Long, flatNumber: String, area: Double, numberOfResidents: Int)

    /**
     * Получить все квартиры пользователя
     */
    fun getFlats(userId: Long): List<FlatDto>

    /**
     * Получить все услуги по квартире
     */
    fun getPublicServices(flatId: Long): List<PublicServiceDto>

    /**
     * Добавить показания
     */
    fun addMetric(flatId: Long, publicServiceId: Long, value: Double)

    /**
     * Получить квитанцию
     */
    fun getBill(flatId: Long, mounth: Int): ByteArray
}

