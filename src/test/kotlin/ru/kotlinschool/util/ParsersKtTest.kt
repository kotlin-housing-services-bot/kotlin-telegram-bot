package ru.kotlinschool.util

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import ru.kotlinschool.bot.handlers.entities.FlatRegistrationData
import java.time.Year

class ParsersKtTest {

    @Test
    fun parseFlatDataTest() {
        // Given
        val text = " 27\r\n50.5\r\n2"
        // When
        val flatData = parseFlatData(text)
        // Then
        assertEquals(FlatRegistrationData("27", 50.5, 2), flatData)
    }

    @ParameterizedTest
    @ValueSource(strings = ["-1", "-2", "-3", "-4", "-5", "-6", "-7", "-8", "-9", "-10", "-11", "-12"])
    fun parseMonthMessageGetNumberTest(value: String) {
        // Given
        // When
        val meterReadings = parseMonthMessageGetNumber(value)
        // Then
        assertEquals(value.replace("-", "").toInt(), meterReadings)
    }

    @Test
    fun parseYear() {
        // Given
        val year = Year.now().value.toString()
        // When
        val yearParsed = parseYear(year)
        // Then
        assertEquals(2023, yearParsed)
    }
}