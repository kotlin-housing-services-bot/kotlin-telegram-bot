package ru.kotlinschool.service

import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UserServiceImplTest : DataTest() {

    private val userService = UserServiceImpl(
        managementCompanyRep,
        houseRep,
        flatRep,
        publicServiceRep,
        metricRep,
        billRep
    )

    @Test
    fun getManagementCompaniesTest(){
        val mc = userService.getManagementCompanies()
        Assertions.assertTrue { mc.size == 1 }
    }

    @Test
    fun getHouses() {
        val houses = userService.getHouses(1L)
        Assertions.assertTrue { houses.size == 1 }
    }

    @Test
    fun registerFlat(){
        userService.registerFlat(1L, 1L,1L, "99", 54.0, 2)
        verify(exactly = 1) { flatRep.save(any()) }
    }

    @Test
    fun getFlats() {
        val flats = userService.getFlats(1L)
        Assertions.assertTrue { flats.size == 2 }
    }

    @Test
    fun getPublicServices(){
        val services = userService.getPublicServices(1L)
        Assertions.assertTrue { services.size == 3 }
    }

    @Test
    fun addMetric(){
        userService.addMetric(1L, 1L,145.0)
        verify(exactly = 1) { metricRep.save(any()) }
    }

    @Test
    fun getBill() {
        val bill = userService.getBill(1L, 2023, 1)
        Assertions.assertNotNull {  bill }
    }
}
