package ru.kotlinschool.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.kotlinschool.data.CalculateData
import ru.kotlinschool.exception.ValidationException
import java.math.BigDecimal


class MetricCalculationStrategyTest {

    private val calculationStrategy = MetricCalculationStrategy()

    @Test
    fun executeTest() {
        val calculateData = CalculateData(BigDecimal.valueOf(40), 50.0, 2)
        assertThrows<ValidationException> {
            calculationStrategy.execute(calculateData)
        }
    }
}
