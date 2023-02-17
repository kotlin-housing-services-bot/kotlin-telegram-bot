package ru.kotlinschool.dto

import ru.kotlinschool.persistent.entity.CalculationType
import java.math.BigDecimal

data class BillServiceData(
    /**
     * Наименование услуги
     */
    val name: String,

    /**
     * Единица измерения
     */
    val unit: String,

    /**
     * Тип расчета
     */
    val calculationType: CalculationType,

    /**
     * Тариф
     */
    val rate: BigDecimal,

    /**
     * Текущие показания
     */
    var metricCurrent: Double? = null,

    /**
     * Предыдущие показания
     */
    var metricPrevious: Double? = null
)

