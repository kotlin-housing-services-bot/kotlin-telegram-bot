package ru.kotlinschool.service

import org.springframework.stereotype.Service
import ru.kotlinschool.dto.CalculateData
import ru.kotlinschool.dto.CalculationResult
import ru.kotlinschool.persistent.entity.CalculationType
import ru.kotlinschool.persistent.entity.CalculationType.BY_METER
import ru.kotlinschool.persistent.entity.CalculationType.BY_FLAT_AREA
import ru.kotlinschool.persistent.entity.CalculationType.BY_NUMBER_OF_RESDENTS
import ru.kotlinschool.persistent.entity.CalculationType.BY_MONTHLY_RATE

@Service
class CalculationServiceImpl : CalculationService {
    private val operations = mapOf(
        BY_METER to MetricCalculationStrategy(),
        BY_FLAT_AREA to AreaCalculationStrategy(),
        BY_NUMBER_OF_RESDENTS to ResidentsCalculationStrategy(),
        BY_MONTHLY_RATE to MonthlyCalculationStrategy()
    )

    override fun execute(type: CalculationType, data: CalculateData): CalculationResult {
        return operations[type]!!.execute(data)
    }

}
