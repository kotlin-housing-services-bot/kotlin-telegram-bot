package ru.kotlinschool.service

import ru.kotlinschool.dto.CalculateData
import ru.kotlinschool.dto.CalculationResult
import ru.kotlinschool.persistent.entity.CalculationType
import java.math.BigDecimal

interface CalculationService {

    /**
     * Вычислить сумму платежа
     */
    fun execute(type: CalculationType, data: CalculateData): CalculationResult

}
