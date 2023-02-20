package ru.kotlinschool.service

import ru.kotlinschool.data.CalculateData
import ru.kotlinschool.data.CalculationResultData
import ru.kotlinschool.exception.validate
import java.math.BigDecimal

interface CalculationStrategy {

    fun execute(data: CalculateData): CalculationResultData

}

class MetricCalculationStrategy : CalculationStrategy {

    override fun execute(data: CalculateData): CalculationResultData {
        val volume = BigDecimal.valueOf(
            validate(data.metricCurrent, "Не найдены текущие показания") { it != null }
                    - validate(data.metricPrevious, "Не найдены предыдущие показания") { it != null })
        return CalculationResultData(data.rate.multiply(volume), volume)
    }

}

class AreaCalculationStrategy : CalculationStrategy {

    override fun execute(data: CalculateData): CalculationResultData {
        val volume = BigDecimal.valueOf(validate(data.area, "Нет данных о площади квартиры") { it != null })
        return CalculationResultData(data.rate.multiply(volume), volume)
    }

}

class ResidentsCalculationStrategy : CalculationStrategy {

    override fun execute(data: CalculateData): CalculationResultData {
        return CalculationResultData(
            data.rate.multiply(
                BigDecimal.valueOf(validate(
                    data.numberOfResidents,
                    "Нет данных о количестве прописанных"
                ) { it != null })
            )
        )
    }

}

class MonthlyCalculationStrategy : CalculationStrategy {

    override fun execute(data: CalculateData): CalculationResultData {
        return CalculationResultData(data.rate)
    }

}
