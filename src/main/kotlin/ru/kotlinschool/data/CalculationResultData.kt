package ru.kotlinschool.data

import java.math.BigDecimal

data class CalculationResultData(
    /**
     * Сумма
     */
    val sum: BigDecimal,

    /**
     * Объем
     */
    val volume: BigDecimal? = null

)
