package ru.kotlinschool.service

import ru.kotlinschool.data.BillServiceResultData
import ru.kotlinschool.data.HouseData
import ru.kotlinschool.data.PublicServiceData
import ru.kotlinschool.data.UserData
import java.math.BigDecimal

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
    fun getHouses(adminId: Long): List<HouseData>

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
    fun getPublicServices(houseId: Long): List<PublicServiceData>

    /**
     * Внесение тарифа для услуги
     */
    fun setRate(publicServiceId: Long, value: BigDecimal)

    /**
     * Все собственники квартир
     */
    fun getUsers(houseId: Long): List<UserData>

    /**
     * Посчитать квитанции
     */
    fun calculateBills(houseId: Long): List<BillServiceResultData>

}
