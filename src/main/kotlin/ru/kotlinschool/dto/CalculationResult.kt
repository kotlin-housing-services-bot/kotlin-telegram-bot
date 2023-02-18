package ru.kotlinschool.dto

import java.math.BigDecimal

data class CalculationResult(
    /**
     * Сумма
     */
    val sum: BigDecimal,

    /**
     * Объем
     */
    val volume: BigDecimal? = null

    )
