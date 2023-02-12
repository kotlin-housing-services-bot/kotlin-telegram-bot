package ru.kotlinschool.util

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource


class ExcelTest {

    @Test
    fun addCustomer() {
        val excel = Excel()
        excel.addCustomer("Иван Иванов", 12, 48.6, 2)
        excel.save("addCustomer")
        assertTrue(ClassPathResource("addCustomer.xlsx").exists())
    }

    @Test
    fun addCompany() {
        val excel = Excel()
        excel.addManagementCompany(1, "Горчищник", "Пушкина", "123456789101")
        excel.save("addManagementCompany")
        assertTrue(ClassPathResource("addManagementCompany.xlsx").exists())
    }

    @Test
    fun addPublicService() {
        val excel = Excel()
        excel.addPublicService(1, "Вода", "м3", 3, 21.5)
        excel.addPublicService(2, "Свет", "кв*Ч", 87, 7.4)
        excel.save("addPublicService")
        assertTrue(ClassPathResource("addPublicService.xlsx").exists())
    }

    @Test
    fun excel() {
        val excel = Excel()
        excel.addCustomer("Иван Иванов", 12, 48.6, 2)
        excel.addManagementCompany(1, "Горчищник", "Пушкина", "123456789101")
        excel.addPublicService(1, "Вода", "м3", 3, 21.5)
        excel.addPublicService(2, "Свет", "кв*Ч", 87, 7.4)
        excel.save("res")
        assertTrue(ClassPathResource("res.xlsx").exists())
    }
}
