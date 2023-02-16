package ru.kotlinschool.service

import ru.kotlinschool.dto.CalculateData
import ru.kotlinschool.dto.CalculationResult
import java.math.BigDecimal

interface CalculationStrategy {

    fun execute(data: CalculateData): CalculationResult

}

class MetricCalculationStrategy : CalculationStrategy {

    override fun execute(data: CalculateData): CalculationResult {
        val volume = BigDecimal.valueOf(data.metricCurrent!! - data.metricPrevious!!)
        return CalculationResult(data.rate.multiply(volume), volume)
    }

}

class AreaCalculationStrategy : CalculationStrategy {

    override fun execute(data: CalculateData): CalculationResult {
        val volume = BigDecimal.valueOf(data.area!!)
        return CalculationResult(data.rate.multiply(volume), volume)
    }

}

class ResidentsCalculationStrategy : CalculationStrategy {

    override fun execute(data: CalculateData): CalculationResult {
        return CalculationResult(BigDecimal.valueOf(data.numberOfResidents!!))
    }

}

class MonthlyCalculationStrategy : CalculationStrategy {

    override fun execute(data: CalculateData): CalculationResult {
        return CalculationResult(data.rate)
    }

}
