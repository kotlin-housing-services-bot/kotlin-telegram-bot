package ru.kotlinschool.service

import ru.kotlinschool.dto.FlatDto
import ru.kotlinschool.dto.HouseDto
import ru.kotlinschool.dto.PublicServiceDto
import ru.kotlinschool.dto.UserDto
import java.math.BigDecimal
import java.time.LocalDate

interface AdminService {

    /**
     * Проверка, что пользователь является админом
     */
    fun isAdmin(userId: Long): Boolean

    /**
     * Добавление УК
     */
    fun registerManagementCompany(adminId: Long, name: String, inn: String)

    /**
     * Возвращает все дома УК(одному администратору соответствует одна УК)
     */
    fun getHouses(adminId: Long): List<HouseDto>

    /**
     * Добавление дома(одному администратору соответствует одна УК)
     */
    fun registerHouse(adminId: Long, address: String)

    /**
     * Добавление коммунальной услуги
     */
    fun registerPublicService(houseId: Long, name: String, calculationType: String, unit: String)

    /**
     * Получить все услуги по дому
     */
    fun getPublicServices(houseId: Long): List<PublicServiceDto>

    /**
     * Внесение тарифа для услуги
     */
    fun setRate(publicServiceId: Long, value: BigDecimal)

    /**
     * Все собственники квартир
     */
    fun getUsers(houseId: Long): List<UserDto>

    /**
     * Посчитать квитанции
     */
    fun calculateBills(houseId: Long)

}
