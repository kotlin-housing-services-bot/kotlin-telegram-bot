package ru.kotlinschool.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import org.junit.jupiter.api.BeforeEach
import ru.kotlinschool.persistent.entity.Bill
import ru.kotlinschool.persistent.entity.CalculationType
import ru.kotlinschool.persistent.entity.Flat
import ru.kotlinschool.persistent.entity.House
import ru.kotlinschool.persistent.entity.ManagementCompany
import ru.kotlinschool.persistent.entity.Metric
import ru.kotlinschool.persistent.entity.PublicService
import ru.kotlinschool.persistent.entity.Rate
import ru.kotlinschool.persistent.repository.BillRepository
import ru.kotlinschool.persistent.repository.FlatRepository
import ru.kotlinschool.persistent.repository.HouseRepository
import ru.kotlinschool.persistent.repository.ManagementCompanyRepository
import ru.kotlinschool.persistent.repository.MetricRepository
import ru.kotlinschool.persistent.repository.PublicServiceRepository
import ru.kotlinschool.persistent.repository.RateRepository
import ru.kotlinschool.util.ExcelBuilder
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*

open class DataTest {

    val managementCompanyRep = mockk<ManagementCompanyRepository>()
    val houseRep = mockk<HouseRepository>()
    val rateRep = mockk<RateRepository>()
    val publicServiceRep = mockk<PublicServiceRepository>()
    val billRep = mockk<BillRepository>()
    val flatRep = mockk<FlatRepository>()
    val metricRep = mockk<MetricRepository>()
    val bill = mockk<Bill>()
    val managementCompany = mockk<ManagementCompany>()
    val house = mockk<House>()
    val flat1 = mockk<Flat>()
    val flat2 = mockk<Flat>()
    val flat3 = mockk<Flat>()
    val service1 = mockk<PublicService>()
    val service2 = mockk<PublicService>()
    val service3 = mockk<PublicService>()
    val service4 = mockk<PublicService>()
    val rate11 = mockk<Rate>()
    val rate12 = mockk<Rate>()
    val rate21 = mockk<Rate>()
    val rate22 = mockk<Rate>()
    val rate31 = mockk<Rate>()
    val metric111 = mockk<Metric>()
    val metric112 = mockk<Metric>()
    val metric113 = mockk<Metric>()
    val metric114 = mockk<Metric>()
    val metric121 = mockk<Metric>()
    val metric122 = mockk<Metric>()
    val metric123 = mockk<Metric>()
    val metric124 = mockk<Metric>()
    val metric211 = mockk<Metric>()
    val metric212 = mockk<Metric>()
    val metric213 = mockk<Metric>()
    val metric221 = mockk<Metric>()
    val metric222 = mockk<Metric>()
    val metric223 = mockk<Metric>()
    val metric311 = mockk<Metric>()
    val metric312 = mockk<Metric>()
    val metric313 = mockk<Metric>()
    val metric314 = mockk<Metric>()
    val metric321 = mockk<Metric>()
    val metric322 = mockk<Metric>()
    val excelBuilder = mockk<ExcelBuilder>()

    @BeforeEach
    fun setUp() {
        every { bill.billData } returns byteArrayOf(10, 2, 15, 11)
        every { managementCompany.name } returns "УК УЮТ"
        every { managementCompany.id } returns 1L
        every { house.id } returns 1L
        every { house.address } returns "г. Москва, ул. Тверская, д. 13"
        every { house.managementCompany } returns managementCompany
        every { house.flats } returns listOf(flat1, flat2, flat3)
        every { house.publicServices } returns listOf(service1, service2, service3)
        every { rate11.publicService } returns service1
        every { rate11.sum } returns BigDecimal.valueOf(3.5)
        every { rate11.dateBegin } returns LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
        every { rate12.publicService } returns service1
        every { rate12.sum } returns BigDecimal.TEN
        every { rate12.dateBegin } returns LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).minusMonths(1)
        every { rate21.publicService } returns service2
        every { rate21.sum } returns BigDecimal.valueOf(12)
        every { rate21.dateBegin } returns LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
        every { rate22.publicService } returns service2
        every { rate22.sum } returns BigDecimal.valueOf(7)
        every { rate22.dateBegin } returns LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).minusMonths(1)
        every { rate31.publicService } returns service3
        every { rate31.sum } returns BigDecimal.valueOf(95)
        every { rate31.dateBegin } returns LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).minusMonths(5)
        every { service1.rates } returns listOf(rate11, rate12)
        every { service2.rates } returns listOf(rate21, rate22)
        every { service3.rates } returns listOf(rate31)
        every { service1.id } returns 1L
        every { service1.name } returns "Электроэнергия"
        every { service1.house } returns house
        every { service1.unit } returns "кВт.ч"
        every { service1.calculationType } returns CalculationType.BY_METER
        every { service2.name } returns "Кап. ремонт"
        every { service2.id } returns 2L
        every { service2.house } returns house
        every { service2.calculationType } returns CalculationType.BY_FLAT_AREA
        every { service2.unit } returns "м2"
        every { service3.name } returns "Домофон"
        every { service3.id } returns 3L
        every { service3.house } returns house
        every { service3.calculationType } returns CalculationType.BY_MONTHLY_RATE
        every { service3.unit } returns "мес"
        fillFlat()
        fillMetrics1()
        fillMetrics2()
        fillMetrics3()
        repositorySettings()
    }

    fun fillFlat() {
        every { flat1.id } returns 1L
        every { flat2.id } returns 2L
        every { flat3.id } returns 3L
        every { flat1.number } returns "1"
        every { flat2.number } returns "2"
        every { flat3.number } returns "3"
        every { flat1.userId } returns 1L
        every { flat2.userId } returns 1L
        every { flat3.userId } returns 2L
        every { flat1.chatId } returns 1L
        every { flat2.chatId } returns 1L
        every { flat3.chatId } returns 2L
        every { flat1.house } returns house
        every { flat2.house } returns house
        every { flat3.house } returns house
        every { flat1.area } returns 50.0
        every { flat2.area } returns 34.0
        every { flat3.area } returns 85.0
        every { flat1.numberOfResidents } returns 2
        every { flat2.numberOfResidents } returns 1
        every { flat3.numberOfResidents } returns 3
    }

    fun repositorySettings(){
        every { billRep.save(any()) } returns bill
        every { billRep.findBill(1L, 2023, 1) } returns bill
        every { houseRep.findById(any()) } returns Optional.of(house)
        every { publicServiceRep.findById(1L) } returns Optional.of(service1)
        every { publicServiceRep.findById(2L) } returns Optional.empty()
        every { rateRep.save(any()) } returns rate11
        every { publicServiceRep.save(any()) } returns service4
        every { houseRep.save(any()) } returns house
        every { flatRep.save(any()) } returns flat1
        every { flatRep.findByUserId(1L) } returns listOf(flat1, flat2)
        every { flatRep.findById(1L) } returns Optional.of(flat1)
        every { managementCompanyRep.findByUserId(1L) } returns managementCompany
        every { managementCompanyRep.findAll() } returns listOf(managementCompany)
        every { managementCompanyRep.save(any()) } returns managementCompany
        every { houseRep.findHousesByAdminId(1L) } returns listOf(house)
        every { houseRep.findHousesByManagementCompany(1L) } returns listOf(house)
        every { metricRep.save(any()) } returns metric111
        mockkConstructor(ExcelBuilder::class)
        every { anyConstructed<ExcelBuilder>().data(any()) } returns excelBuilder
        every { excelBuilder.build() } returns byteArrayOf(10, 2, 15, 11)
    }

    fun fillMetrics1() {
        every { metric111.publicService } returns service1
        every { metric111.flat } returns flat1
        every { metric111.value } returns 2.0
        every { metric111.actionDate } returns LocalDate.now()
        every { metric111.isInit } returns false
        every { metric112.publicService } returns service1
        every { metric112.flat } returns flat1
        every { metric112.value } returns 3.0
        every { metric112.actionDate } returns LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())
        every { metric112.isInit } returns false
        every { metric113.publicService } returns service1
        every { metric113.flat } returns flat1
        every { metric113.value } returns 2.5
        every { metric113.actionDate } returns LocalDate.now().minusMonths(1)
        every { metric113.isInit } returns false
        every { metric114.publicService } returns service1
        every { metric114.flat } returns flat1
        every { metric114.value } returns 3.5
        every { metric114.actionDate } returns LocalDate.now().minusMonths(1)
            .with(TemporalAdjusters.firstDayOfMonth()).plusDays(5)
        every { metric114.isInit } returns false
        every { metric121.publicService } returns service2
        every { metric121.flat } returns flat1
        every { metric121.value } returns 1.0
        every { metric121.actionDate } returns LocalDate.now()
        every { metric121.isInit } returns false
        every { metric122.publicService } returns service2
        every { metric122.flat } returns flat1
        every { metric122.value } returns 1.5
        every { metric122.actionDate } returns LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())
        every { metric122.isInit } returns false
        every { metric123.publicService } returns service2
        every { metric123.flat } returns flat1
        every { metric123.value } returns 2.5
        every { metric123.actionDate } returns LocalDate.now().minusMonths(1)
        every { metric123.isInit } returns false
        every { metric124.publicService } returns service2
        every { metric124.flat } returns flat1
        every { metric124.value } returns 1.5
        every { metric124.actionDate } returns LocalDate.now().minusMonths(1)
            .with(TemporalAdjusters.firstDayOfMonth()).plusDays(20)
        every { metric124.isInit } returns false
        every { flat1.metrics } returns listOf(
            metric111, metric112, metric113, metric114, metric121, metric122,
            metric123, metric124
        )
    }


    fun fillMetrics2() {
        every { metric211.publicService } returns service1
        every { metric211.flat } returns flat2
        every { metric211.value } returns 1.0
        every { metric211.actionDate } returns LocalDate.now()
        every { metric211.isInit } returns false
        every { metric212.publicService } returns service1
        every { metric212.flat } returns flat2
        every { metric212.value } returns 1.5
        every { metric212.actionDate } returns LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
            .plusDays(3)
        every { metric212.isInit } returns false
        every { metric213.publicService } returns service1
        every { metric213.flat } returns flat2
        every { metric213.value } returns 2.5
        every { metric213.actionDate } returns LocalDate.now().minusMonths(1)
        every { metric213.isInit } returns false
        every { metric221.publicService } returns service2
        every { metric221.flat } returns flat2
        every { metric221.value } returns 7.0
        every { metric221.actionDate } returns LocalDate.now()
        every { metric221.isInit } returns false
        every { metric222.publicService } returns service2
        every { metric222.flat } returns flat2
        every { metric222.value } returns 6.0
        every { metric222.actionDate } returns LocalDate.now()
        every { metric222.isInit } returns false
        every { metric223.publicService } returns service2
        every { metric223.flat } returns flat2
        every { metric223.value } returns 6.5
        every { metric223.actionDate } returns LocalDate.now()
        every { metric223.isInit } returns true
        every { flat2.metrics } returns listOf(
            metric211, metric212, metric213, metric221, metric222, metric223
        )

    }

    fun fillMetrics3() {
        every { metric311.publicService } returns service1
        every { metric311.flat } returns flat3
        every { metric311.value } returns 10.0
        every { metric311.actionDate } returns LocalDate.now()
        every { metric311.isInit } returns false
        every { metric312.publicService } returns service1
        every { metric312.flat } returns flat3
        every { metric312.value } returns 11.5
        every { metric312.actionDate } returns LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
            .plusDays(10)
        every { metric312.isInit } returns false
        every { metric313.publicService } returns service1
        every { metric313.flat } returns flat3
        every { metric313.value } returns 12.5
        every { metric313.actionDate } returns LocalDate.now().minusMonths(1)
        every { metric313.isInit } returns false
        every { metric314.publicService } returns service1
        every { metric314.flat } returns flat3
        every { metric314.value } returns 12.0
        every { metric314.actionDate } returns LocalDate.now().minusMonths(1)
            .with(TemporalAdjusters.firstDayOfMonth()).plusDays(7)
        every { metric314.isInit } returns false
        every { metric321.publicService } returns service2
        every { metric321.flat } returns flat3
        every { metric321.value } returns 9.0
        every { metric321.actionDate } returns LocalDate.now()
        every { metric321.isInit } returns true
        every { metric322.publicService } returns service2
        every { metric322.flat } returns flat3
        every { metric322.value } returns 8.5
        every { metric322.actionDate } returns LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())
        every { metric322.isInit } returns false
        every { flat3.metrics } returns listOf(
            metric311, metric312, metric313, metric314, metric321, metric322)
    }

}
