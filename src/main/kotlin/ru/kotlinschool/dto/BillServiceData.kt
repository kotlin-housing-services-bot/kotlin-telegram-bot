package ru.kotlinschool.dto

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
     * Тариф
     */
    val rate: BigDecimal
)

