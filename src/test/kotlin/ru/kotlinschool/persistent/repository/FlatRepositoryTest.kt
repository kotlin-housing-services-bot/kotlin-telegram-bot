package ru.kotlinschool.persistent.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import ru.kotlinschool.persistent.entity.Flat
import ru.kotlinschool.persistent.entity.House
import ru.kotlinschool.persistent.entity.ManagementCompany

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FlatRepositoryTest {

    @Autowired
    private lateinit var managementCompanyRep: ManagementCompanyRepository
    @Autowired
    private lateinit var houseRep: HouseRepository
    @Autowired
    private lateinit var flatRep: FlatRepository

    @Test
    fun findByUserIdSuccessTest() {

        val managementCompany = managementCompanyRep.save(ManagementCompany(name = "УК", inn = "123456789111", userId = 1L))
        val house = houseRep.save(House(managementCompany = managementCompany, address = "г. Москва, ул. Велозаводская, д. 6а", chatId = 1L))
        val flat1 = flatRep.save(Flat(house = house, number = "1", area = 60.0, numberOfResidents = 2, userId = 3L))
        val flat2 = flatRep.save(Flat(house = house, number = "3", area = 40.0, numberOfResidents = 1, userId = 4L))

        // when
        val foundEntity = flatRep.findByUserId(4L)
        // then
        Assertions.assertTrue { foundEntity.get(0) == flat2 }
    }

}