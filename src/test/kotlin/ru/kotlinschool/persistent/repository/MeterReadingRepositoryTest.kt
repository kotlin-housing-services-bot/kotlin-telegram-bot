package ru.kotlinschool.persistent.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import ru.kotlinschool.persistent.entity.ManagementCompany
import ru.kotlinschool.persistent.entity.House
import ru.kotlinschool.persistent.entity.Flat
import ru.kotlinschool.persistent.entity.PublicService
import ru.kotlinschool.persistent.entity.CalculationType
import ru.kotlinschool.persistent.entity.MeterReading
import ru.kotlinschool.persistent.entity.Rate
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
    private lateinit var serviceRep: PublicServiceRepository

    @Autowired
    private lateinit var rateRep: RateRepository

    @Autowired
    private lateinit var meterReadingRep: MeterReadingRepository

    @Test
    fun findByFlatAndPublicServiceTest() {
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
        val flat = flatRep.save(
            Flat(
                house = house, number = "1", area = 60.0, numberOfResidents = 2,
                userId = 3L
            )
        )
        val service = serviceRep.save(
            PublicService(
                house = house, name = "Свет",
                calculationType = CalculationType.BY_METER
            )
        )
        rateRep.save(Rate(publicService = service, sum = 3.6, dateBegin = LocalDate.now()))
        val meterReading1 = meterReadingRep.save(
            MeterReading(
                flat = flat, publicService = service, value = 99.5,
                actionDate = LocalDate.now()
            )
        )
        meterReadingRep.save(
            MeterReading(
                flat = flat, publicService = service, value = 77.5,
                actionDate = LocalDate.now().minusDays(1)
            )
        )

        // when
        val foundEntity = meterReadingRep.findFirstByFlatAndPublicServiceOrderByActionDateDesc(flat, service)
        // then
        Assertions.assertTrue { foundEntity == meterReading1 }

        // when
        val meterReadings = meterReadingRep.findByFlatAndPublicService(flat, service)
        // then
        Assertions.assertTrue { meterReadings.size == 2 }
    }

}
