package ru.kotlinschool.service

import org.springframework.stereotype.Service
import ru.kotlinschool.data.CalculateData
import ru.kotlinschool.data.CalculationResultData
import ru.kotlinschool.persistent.entity.CalculationType
import ru.kotlinschool.persistent.entity.CalculationType.BY_METER
import ru.kotlinschool.persistent.entity.CalculationType.BY_FLAT_AREA
import ru.kotlinschool.persistent.entity.CalculationType.BY_NUMBER_OF_RESIDENTS
import ru.kotlinschool.persistent.entity.CalculationType.BY_MONTHLY_RATE

@Service
class CalculationServiceImpl : CalculationService {
        private var operations = mapOf(
            BY_METER to MetricCalculationStrategy(),
            BY_FLAT_AREA to AreaCalculationStrategy(),
            BY_NUMBER_OF_RESIDENTS to ResidentsCalculationStrategy(),
            BY_MONTHLY_RATE to MonthlyCalculationStrategy()
        )

    override fun execute(type: CalculationType, data: CalculateData): CalculationResultData {
        return operations[type]!!.execute(data)
    }

}
