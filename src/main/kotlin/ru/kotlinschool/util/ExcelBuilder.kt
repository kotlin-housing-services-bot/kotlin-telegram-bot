package ru.kotlinschool.util

import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.core.io.ClassPathResource
import ru.kotlinschool.data.BillData
import ru.kotlinschool.data.CalculateData
import ru.kotlinschool.service.CalculationService
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.math.BigDecimal
import java.math.RoundingMode

class ExcelBuilder(
     private val calculationService: CalculationService
) {
    private val billTemplate = "bill-template.xlsx"
    private val file = FileInputStream(ClassPathResource(billTemplate).file)
    private val workbook: Workbook = XSSFWorkbook(file)
    private val sheet: Sheet = workbook.getSheetAt(0)
    private var companyRow = 4
    private var publicServiceRow = 8
    private var resultSum = BigDecimal.ZERO

    fun data(billData: BillData): ExcelBuilder {
        billData.run {
            addManagementCompany(1, managementCompanyName)
            addOwner(address, area!!, numberOfResidents!!)
            services.forEachIndexed { i, billServiceData ->
                 billServiceData.run {
                    val calculateData = CalculateData(rate, area, numberOfResidents, metricCurrent, metricPrevious)
                    val (volume, sum) = calculationService.execute(calculationType, calculateData).run { volume to sum }
                    addPublicService(i, name, unit, rate, sum, volume)
                }
            }
        }
        return this
    }

    fun build(): ByteArray =
        workbook.use { wb ->
            return ByteArrayOutputStream().use {
                wb.write(it)
                it.toByteArray()
            }
        }


    private fun addManagementCompany(number: Long, name: String) {
        var companyColumnIndex = 4
        sheet.getRow(companyRow).apply {
            getCell(companyColumnIndex++).setCellValue(number)
            getCell(companyColumnIndex++).setCellValue(name)
        }
    }

    private fun addOwner(address: String, flatArea: Double, residentNumber: Long) {
        var startRow = 2
        val customerColumnIndex = 2

        sheet.getRow(startRow++).getCell(customerColumnIndex).setCellValue(address)
        sheet.getRow(startRow++).getCell(customerColumnIndex).setCellValue(flatArea)
        sheet.getRow(startRow).getCell(customerColumnIndex).setCellValue(residentNumber)
    }

    private fun addPublicService(
        number: Int,
        name: String,
        unit: String,
        rate: BigDecimal,
        sum: BigDecimal,
        volume: BigDecimal? = null
    ) {
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
                setCellValue(rate.toStringScale2())
            }
            createCell(customerColumnIndex++).apply {
                cellStyle = style
                setCellValue(sum.toStringScale2())
            }
        }

        resultSum += sum
        val resultColumnIndex = 5
        sheet.getRow(publicServiceRow).getCell(resultColumnIndex).setCellValue(resultSum.toStringScale2())
    }

    private fun Cell.setCellValue(value: Int) = this.setCellValue(value.toDouble())
    private fun Cell.setCellValue(value: Long) = this.setCellValue(value.toDouble())

    private fun BigDecimal.toStringScale2() = this.setScale(2, RoundingMode.HALF_UP).toString()
}
