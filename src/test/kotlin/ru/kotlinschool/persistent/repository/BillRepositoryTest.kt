package ru.kotlinschool.persistent.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import ru.kotlinschool.persistent.entity.Bill
import ru.kotlinschool.persistent.entity.Flat
import ru.kotlinschool.persistent.entity.House
import ru.kotlinschool.persistent.entity.ManagementCompany
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BillRepositoryTest {

    @Autowired
    private lateinit var managementCompanyRep: ManagementCompanyRepository
    @Autowired
    private lateinit var houseRep: HouseRepository
    @Autowired
    private lateinit var flatRep: FlatRepository
    @Autowired
    private lateinit var billRep: BillRepository
    private var flat: Flat? = null
    private var bill: Bill? = null

    @BeforeEach
    fun setup () {
        val managementCompany = managementCompanyRep.save(
            ManagementCompany(
                name = "УК", inn = "123456789111",
                userId = 1L
            )
        )
        val house = houseRep.save(
            House(
                managementCompany = managementCompany,
                address = "г. Москва, ул. Велозаводская, д. 6а"
            )
        )
        flat = flatRep.save(
            Flat(
                house = house,
                number = "1",
                area = 60.0,
                numberOfResidents = 2,
                userId = 3L,
                chatId = 3L
            )
        )
        bill = billRep.save(
            Bill(
                flat = flat!!,
                year = 2023,
                month = 1,
                File("src/main/resources/bill-template.xlsx").readBytes()
            )
        )
    }


    @Test
    fun findBillTest() {
        val findEntity = billRep.findBill(flat!!.id, 2023, 1)
        Assertions.assertTrue { findEntity == bill}
    }
}
