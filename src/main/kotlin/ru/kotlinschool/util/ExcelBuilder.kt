package ru.kotlinschool.util

import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.core.io.ClassPathResource
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.math.BigDecimal
import java.math.RoundingMode

class ExcelBuilder {
    private val workbook: Workbook
    private val sheet: Sheet
    private var companyRow = 4
    private var publicServiceRow = 9
    private var resultSum = BigDecimal.ZERO

    init {
        val billTemplate = "bill-template.xlsx"
        val file = FileInputStream(ClassPathResource(billTemplate).file)
        workbook = XSSFWorkbook(file)
        sheet = workbook.getSheetAt(0)
    }

    fun addCustomer(fio: String, flatNumber: Int, flatSquare: Double, residentNumber: Int): ExcelBuilder {
        var startRow = 2
        val customerColumnIndex = 2

        sheet.getRow(startRow++).getCell(customerColumnIndex).setCellValue(fio)
        sheet.getRow(startRow++).getCell(customerColumnIndex).setCellValue(flatNumber)
        sheet.getRow(startRow++).getCell(customerColumnIndex).setCellValue(flatSquare)
        sheet.getRow(startRow).getCell(customerColumnIndex).setCellValue(residentNumber)

        return this
    }

    fun addManagementCompany(number: Long, name: String, address: String, inn: String): ExcelBuilder {
        var companyColumnIndex = 4
        sheet.getRow(companyRow).apply {
            getCell(companyColumnIndex++).setCellValue(number)
            getCell(companyColumnIndex++).setCellValue(name)
            getCell(companyColumnIndex++).setCellValue(address)
            getCell(companyColumnIndex++).setCellValue(inn)
        }

        return this
    }

    fun addPublicService(number: Int, name: String, unit: String, volume: BigDecimal, rate: BigDecimal): ExcelBuilder {
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
        val result = volume * rate

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
                setCellValue(volume.toEngineeringString())
            }
            createCell(customerColumnIndex++).apply {
                cellStyle = style
                setCellValue(rate.toStringScale2())
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

    fun save(): ByteArray =
        workbook.use { wb ->
            return@use ByteArrayOutputStream().use {
                wb.write(it)
                it.toByteArray()
            }
        }

    private fun Cell.setCellValue(value: Int) = this.setCellValue(value.toDouble())
    private fun Cell.setCellValue(value: Long) = this.setCellValue(value.toDouble())

    private fun BigDecimal.toStringScale2() = this.setScale(2, RoundingMode.HALF_UP).toString()
}
