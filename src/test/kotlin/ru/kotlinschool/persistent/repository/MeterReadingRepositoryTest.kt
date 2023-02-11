package ru.kotlinschool.persistent.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import ru.kotlinschool.persistent.entity.*
import java.time.LocalDate

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MeterReadingRepositoryTest {

    @Autowired
    private lateinit var managementCompanyRep: ManagementCompanyRepository
    @Autowired
    private lateinit var houseRep: HouseRepository
    @Autowired
    private lateinit var flatRep: FlatRepository
    @Autowired
    private lateinit var serviceRep: ServiceRepository
    @Autowired
    private lateinit var rateRep: RateRepository
    @Autowired
    private lateinit var meterReadingRep: MeterReadingRepository

    @Test
    fun findByUserIdSuccessTest() {
        val managementCompany = managementCompanyRep.save(ManagementCompany(name = "УК", inn = "123456789111", userId = 1L))
        val house = houseRep.save(House(managementCompany = managementCompany, address = "г. Москва, ул. Велозаводская, д. 6а", chatId = 1L))
        val flat = flatRep.save(Flat(house = house, number = "1", area = 60.0, numberOfResidents = 2, userId = 3L))
        val service = serviceRep.save(PublicService(house = house, name = "Свет", calculationType = CalculationType.BY_METER))
        val rate = rateRep.save(Rate(service = service, sum = 3.6, dateBegin = LocalDate.now()))
        val meterReading1 = meterReadingRep.save(MeterReading(flat = flat, service = service, value = 99.5, actionDate = LocalDate.now()))
        val meterReading2 = meterReadingRep.save(MeterReading(flat = flat, service = service, value = 77.5, actionDate = LocalDate.now().minusDays(1)))

        // when
        val foundEntity = meterReadingRep.findFirstByFlatAndServiceOrderByActionDateDesc(flat, service)
        // then
        Assertions.assertTrue { foundEntity == meterReading1 }

        // when
        val meterReadings = meterReadingRep.findByFlatAndService(flat, service)
        // then
        Assertions.assertTrue { meterReadings.size == 2 }
    }
}