package ru.kotlinschool.util

import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import ru.kotlinschool.dto.BillData
import ru.kotlinschool.dto.CalculateData
import ru.kotlinschool.dto.ManagementCompanyDto
import ru.kotlinschool.persistent.entity.CalculationType
import ru.kotlinschool.service.CalculationService
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class ExcelBuilder(
    private val calculationService: CalculationService
) {
    private val workbook: Workbook
    private val sheet: Sheet
    private var companyRow = 4
    private var publicServiceRow = 8
    private var resultSum = BigDecimal.ZERO

    init {
        val billTemplate = "bill-template.xlsx"
        val file = FileInputStream(ClassPathResource(billTemplate).file)
        workbook = XSSFWorkbook(file)
        sheet = workbook.getSheetAt(0)
    }

    fun buildBill(billData: BillData, managementCompany: ManagementCompanyDto): ExcelBuilder {
        billData.run {
            addCustomer(address, area!!, numberOfResidents!!)
            services.forEachIndexed { i, billServiceData ->
                billServiceData.run {
                    val calculateData = CalculateData(rate, area, numberOfResidents, metricCurrent, metricPrevious)
                    addPublicService(i, name, unit, calculationType, calculateData)
                }
            }
        }
        managementCompany.run {
            addManagementCompany(id, name)
        }
        return this
    }

    fun save(): ByteArray =
        workbook.use { wb ->
            return ByteArrayOutputStream().use {
                wb.write(it)
                it.toByteArray()
            }
        }

    private fun addCustomer(address: String, flatSquare: Double, residentNumber: Long): ExcelBuilder {
        var startRow = 2
        val customerColumnIndex = 2

        sheet.getRow(startRow++).getCell(customerColumnIndex).setCellValue(address)
        sheet.getRow(startRow++).getCell(customerColumnIndex).setCellValue(flatSquare)
        sheet.getRow(startRow).getCell(customerColumnIndex).setCellValue(residentNumber)

        return this
    }

    private fun addManagementCompany(number: Long, name: String): ExcelBuilder {
        var companyColumnIndex = 4
        sheet.getRow(companyRow).apply {
            getCell(companyColumnIndex++).setCellValue(number)
            getCell(companyColumnIndex++).setCellValue(name)
        }

        return this
    }

    private fun addPublicService(
        number: Int,
        name: String,
        unit: String,
        type: CalculationType,
        calculateData: CalculateData
    ): ExcelBuilder {
        var customerColumnIndex = 0
        val style = workbook.createCellStyle().apply {
            borderRight = BorderStyle.THIN
            val font = workbook.createFont().apply {
                fontName = "Arial"
                fontHeightInPoints = 5
            }
            alignment = HorizontalAlignment.CENTER
            setFont(font)
        }
        sheet.shiftRows(publicServiceRow, publicServiceRow, 1)
        val (volume, result) = calculationService.execute(type, calculateData).run { volume to sum }

        sheet.createRow(publicServiceRow++).apply {
            createCell(customerColumnIndex++).apply {
                cellStyle = style
                setCellValue(number)
            }
            createCell(customerColumnIndex++).apply {
                cellStyle = style
                setCellValue(name)
            }
            createCell(customerColumnIndex++).apply {
                cellStyle = style
                setCellValue(unit)
            }
            createCell(customerColumnIndex++).apply {
                cellStyle = style
                setCellValue(volume?.toEngineeringString())
            }
            createCell(customerColumnIndex++).apply {
                cellStyle = style
                setCellValue(calculateData.rate.toStringScale2())
            }
            createCell(customerColumnIndex++).apply {
                cellStyle = style
                setCellValue(result.toStringScale2())
            }
        }

        resultSum += result
        val resultColumnIndex = 5
        sheet.getRow(publicServiceRow).getCell(resultColumnIndex).setCellValue(resultSum.toStringScale2())

        return this
    }

    private fun Cell.setCellValue(value: Int) = this.setCellValue(value.toDouble())
    private fun Cell.setCellValue(value: Long) = this.setCellValue(value.toDouble())

    private fun BigDecimal.toStringScale2() = this.setScale(2, RoundingMode.HALF_UP).toString()
}
