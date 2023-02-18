package ru.kotlinschool.dto

import java.math.BigDecimal

data class CalculateData(
    /**
     * Тариф
     */
    val rate: BigDecimal,

    /**
     * Площадь квартиры
     */
    val area: Double? = null,

    /**
     * Количество прописанных
     */
    var numberOfResidents: Long? = null,

    /**
     * Текущие показания
     */
    var metricCurrent: Double? = null,

    /**
     * Предыдущие показания
     */
    var metricPrevious: Double? = null
)

