package ru.kotlinschool.util

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import ru.kotlinschool.service.CalculationService


class ExcelBuilderTest {

    private val calculationService = mockk<CalculationService>()

    @Test
    fun excel() {
        val excelBuilder = ExcelBuilder(calculationService)
        val excelBytes = excelBuilder.save()
        assertNotNull(excelBytes)
    }
}
