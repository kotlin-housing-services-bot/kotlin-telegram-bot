package ru.kotlinschool.service

import ru.kotlinschool.data.CalculateData
import ru.kotlinschool.data.CalculationResultData
import java.math.BigDecimal

interface CalculationStrategy {

    fun execute(data: CalculateData): CalculationResultData

}

class MetricCalculationStrategy : CalculationStrategy {

    override fun execute(data: CalculateData): CalculationResultData {
        val volume = BigDecimal.valueOf(data.metricCurrent!! - data.metricPrevious!!)
        return CalculationResultData(data.rate.multiply(volume), volume)
    }

}

class AreaCalculationStrategy : CalculationStrategy {

    override fun execute(data: CalculateData): CalculationResultData {
        val volume = BigDecimal.valueOf(data.area!!)
        return CalculationResultData(data.rate.multiply(volume), volume)
    }

}

class ResidentsCalculationStrategy : CalculationStrategy {

    override fun execute(data: CalculateData): CalculationResultData {
        return CalculationResultData(data.rate.multiply(BigDecimal.valueOf(data.numberOfResidents!!)))
    }

}

class MonthlyCalculationStrategy : CalculationStrategy {

    override fun execute(data: CalculateData): CalculationResultData {
        return CalculationResultData(data.rate)
    }

}
