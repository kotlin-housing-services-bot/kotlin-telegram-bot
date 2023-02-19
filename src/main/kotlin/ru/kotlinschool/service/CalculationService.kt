package ru.kotlinschool.service

import ru.kotlinschool.data.CalculateData
import ru.kotlinschool.data.CalculationResultData
import ru.kotlinschool.persistent.entity.CalculationType

interface CalculationService {

    /**
     * Вычислить сумму платежа
     */
    fun execute(type: CalculationType, data: CalculateData): CalculationResultData

}
