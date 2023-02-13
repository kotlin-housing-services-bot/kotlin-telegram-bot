package ru.kotlinschool.service

import java.time.LocalDate

interface AdminService {

    /**
     * Внесение тарифа для услуги
     */
    fun setRate(publicServiceId: Long, value: Double, dateBegin: LocalDate)

}
