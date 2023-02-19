package ru.kotlinschool.util

import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import ru.kotlinschool.data.BillData
import ru.kotlinschool.data.BillServiceData
import ru.kotlinschool.persistent.entity.CalculationType
import ru.kotlinschool.service.CalculationServiceImpl
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Paths


class ExcelBuilderTest {

    private val calculationService = spyk(CalculationServiceImpl())

    @Test
    fun excel() {
        val data =   BillData(
            2023,
            1,
            "УК Умный дом",
            "г. Москва, ул. Левобережная, д. 4, кв. 5",
            45.0,
            2,
            listOf(
                BillServiceData(
                    "Электроэнергия",
                    "кВт.ч",
                    CalculationType.BY_METER,
                    BigDecimal.valueOf(45),
                    120.0,
                    10.0
                ),
                BillServiceData(
                    "Кап. ремонт",
                    "м2",
                    CalculationType.BY_FLAT_AREA,
                    BigDecimal.valueOf(11.43)
                ),
                BillServiceData(
                    "Домофон",
                    "мес",
                    CalculationType.BY_MONTHLY_RATE,
                    BigDecimal.valueOf(90)
                )
            )
        )
        val excelBuilder = ExcelBuilder(calculationService)
        val excelBytes = excelBuilder.data(data).build()

        val path = Paths.get("src/test/resources/ExcelBuilderTest.xlsx")
        Files.write(path, excelBytes)

        assertNotNull(excelBytes)
    }
}
