package ru.kotlinschool.persistent.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import ru.kotlinschool.persistent.entity.Bill
import ru.kotlinschool.persistent.entity.House
import ru.kotlinschool.persistent.entity.ManagementCompany

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HouseRepositoryTest {

    @Autowired
    private lateinit var managementCompanyRep: ManagementCompanyRepository
    @Autowired
    private lateinit var houseRep: HouseRepository
    private var managementCompany: ManagementCompany? = null

    @BeforeEach
    fun setup () {
        managementCompany = managementCompanyRep.save(
            ManagementCompany(
                name = "УК", inn = "123456789111",
                userId = 1L
            )
        )
        houseRep.save(
            House(
                managementCompany = managementCompany!!,
                address = "г. Москва, ул. Велозаводская, д. 6а"
            )
        )
        houseRep.save(
            House(
                managementCompany = managementCompany!!,
                address = "г. Москва, ул. Тверская, д. 13"
            )
        )
    }

    @Test
    fun findHousesByManagementCompanyTest() {
        // when
        val foundEntities = houseRep.findHousesByManagementCompany(managementCompany!!.id)
        // then
        Assertions.assertTrue { foundEntities.size == 2 }
    }

    @Test
    fun findHousesTest() {
        // when
        val foundEntities = houseRep.findHousesByAdminId(1L)
        // then
        Assertions.assertTrue { foundEntities.size == 2 }
    }

}
