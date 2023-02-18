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
import ru.kotlinschool.persistent.entity.Metric
import ru.kotlinschool.persistent.entity.Rate
import java.math.BigDecimal
import java.time.LocalDate

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MetricRepositoryTest {

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
    private lateinit var metricep: MetricRepository

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
                house = house,
                number = "1",
                area = 60.0,
                numberOfResidents = 2,
                userId = 3L,
                chatId = 3L
            )
        )
        val service = serviceRep.save(
            PublicService(
                house = house,
                name = "Свет",
                calculationType = CalculationType.BY_METER,
                unit = "кВт.ч"
            )
        )
        rateRep.save(Rate(publicService = service, sum = BigDecimal.TEN, dateBegin = LocalDate.now()))
        val meterReading1 = metricep.save(
            Metric(
                flat = flat, publicService = service, value = 99.5,
                actionDate = LocalDate.now()
            )
        )
        metricep.save(
            Metric(
                flat = flat, publicService = service, value = 77.5,
                actionDate = LocalDate.now().minusDays(1)
            )
        )

        // when
        val foundEntity = metricep.findFirstByFlatAndPublicServiceOrderByActionDateDesc(flat, service)
        // then
        Assertions.assertTrue { foundEntity == meterReading1 }

        // when
        val meterReadings = metricep.findByFlatAndPublicService(flat, service)
        // then
        Assertions.assertTrue { meterReadings.size == 2 }
    }

}
