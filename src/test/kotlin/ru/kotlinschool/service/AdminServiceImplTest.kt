package ru.kotlinschool.service

import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.kotlinschool.exception.EntityNotFoundException
import ru.kotlinschool.persistent.entity.CalculationType
import java.math.BigDecimal


class AdminServiceImplTest : DataTest() {

    private val calculationService = mockk<CalculationServiceImpl>()
    private val adminService = AdminServiceImpl(
        managementCompanyRep,
        houseRep,
        rateRep,
        publicServiceRep,
        billRep,
        calculationService
    )

    @Test
    fun getUsersTest(){
        val users = adminService.getUsers(1L)
        Assertions.assertTrue { users.size == 2 }
    }

    @Test
    fun setRateSuccessTest(){
        adminService.setRate(1L, BigDecimal.TEN)
        verify(exactly = 1) { rateRep.save(any()) }
    }

    @Test
    fun setRateErrorTest(){
        assertThrows<EntityNotFoundException> {
            adminService.setRate(2L, BigDecimal.ONE)
        }
     }

    @Test
    fun getPublicServicesTest(){
        val services = adminService.getPublicServices(1L)
        Assertions.assertTrue { services.size == 3 }
    }

    @Test
    fun registerPublicServiceTest(){
        adminService.registerPublicService(4L, "Отопление", CalculationType.BY_FLAT_AREA.toString(), "м2")
        verify(exactly = 1) { publicServiceRep.save(any()) }
    }

    @Test
    fun registerHouseTest(){
        adminService.registerHouse(1L, "Адрес")
        verify(exactly = 1) { houseRep.save(any()) }
    }

    @Test
    fun getHousesTest(){
        val houses = adminService.getHouses(1L)
        Assertions.assertTrue { houses.size == 1 }
    }

    @Test
    fun registerManagementCompanyTest(){
        adminService.registerManagementCompany(1L, "ТСЖ Наш дом", "11111111")
        verify(exactly = 1) { managementCompanyRep.save(any()) }
    }

    @Test
    fun isAdminTest(){
        val isAdmin = adminService.isAdmin(1L)
        Assertions.assertTrue { isAdmin }
    }
}
