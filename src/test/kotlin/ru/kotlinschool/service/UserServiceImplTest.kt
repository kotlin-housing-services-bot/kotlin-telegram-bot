package ru.kotlinschool.service

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
}
