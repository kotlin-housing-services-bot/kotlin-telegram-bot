package ru.kotlinschool.util

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal


class ExcelBuilderTest {

    @Test
    fun addCustomer() {
        val excelBuilder = ExcelBuilder()
        excelBuilder.addCustomer("Иван Иванов", 12, 48.6, 2)
        val excelBytes = excelBuilder.save()
        assertNotNull(excelBytes)
    }

    @Test
    fun addCompany() {
        val excelBuilder = ExcelBuilder()
        excelBuilder.addManagementCompany(1, "Горчищник", "Пушкина", "123456789101")
        val excelBytes = excelBuilder.save()
        assertNotNull(excelBytes)
    }

    @Test
    fun addPublicService() {
        val excelBuilder = ExcelBuilder()
        excelBuilder.addPublicService(1, "Вода", "м3", BigDecimal.valueOf(3.1), BigDecimal.valueOf(21.5))
        excelBuilder.addPublicService(2, "Свет", "кв*Ч", BigDecimal.valueOf(87), BigDecimal.valueOf(7.4))
        val excelBytes = excelBuilder.save()
        assertNotNull(excelBytes)
    }

    @Test
    fun excel() {
        val excelBuilder = ExcelBuilder()
        excelBuilder.addCustomer("Иван Иванов", 12, 48.6, 2)
        excelBuilder.addManagementCompany(1, "Горчищник", "Пушкина", "123456789101")
        excelBuilder.addPublicService(1, "Вода", "м3", BigDecimal.valueOf(3.1), BigDecimal.valueOf(21.5))
        excelBuilder.addPublicService(2, "Свет", "кв*Ч", BigDecimal.valueOf(87.45), BigDecimal.valueOf(7.4))
        val excelBytes = excelBuilder.save()
        assertNotNull(excelBytes)
    }
}
