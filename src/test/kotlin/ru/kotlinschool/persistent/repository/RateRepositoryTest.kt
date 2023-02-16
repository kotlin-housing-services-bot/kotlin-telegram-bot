package ru.kotlinschool.persistent.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ru.kotlinschool.persistent.entity.CalculationType
import ru.kotlinschool.persistent.entity.Flat
import ru.kotlinschool.persistent.entity.House
import ru.kotlinschool.persistent.entity.ManagementCompany
import ru.kotlinschool.persistent.entity.Metric
import ru.kotlinschool.persistent.entity.PublicService
import ru.kotlinschool.persistent.entity.Rate
import java.math.BigDecimal
import java.time.LocalDate

class RateRepositoryTest {


    @Autowired
    private lateinit var managementCompanyRep: ManagementCompanyRepository

    @Autowired
    private lateinit var houseRep: HouseRepository

    @Autowired
    private lateinit var serviceRep: PublicServiceRepository

    @Autowired
    private lateinit var rateRep: RateRepository

    @Test
    fun findFirstByPublicServiceOrderByDateBeginDescTest() {
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
        val service = serviceRep.save(
            PublicService(
                house = house, name = "Свет",
                calculationType = CalculationType.BY_METER,
                unit = "кВт.ч"
            )
        )
        val rate = rateRep.save(
            Rate(
                publicService = service,
                sum = BigDecimal.TEN,
                dateBegin = LocalDate.now())
        )
        rateRep.save(
            Rate(
                publicService = service,
                sum = BigDecimal.ONE,
                dateBegin = LocalDate.now().minusMonths(1))
        )

        // when
        val foundEntity = rateRep.findFirstByPublicServiceOrderByDateBeginDesc(service)
        // then
        Assertions.assertTrue { foundEntity == rate }
    }
}