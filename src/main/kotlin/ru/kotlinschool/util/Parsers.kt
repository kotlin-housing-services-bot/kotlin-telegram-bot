package ru.kotlinschool.util

import ru.kotlinschool.bot.handlers.entities.FlatRegistrationData
import ru.kotlinschool.bot.handlers.entities.MappedRatesData
import ru.kotlinschool.bot.handlers.entities.MappedRegistrationData
import ru.kotlinschool.bot.ui.flatAreaErrorMessage
import ru.kotlinschool.bot.ui.flatNumErrorMessage
import ru.kotlinschool.bot.ui.formatErrorMessage
import ru.kotlinschool.bot.ui.monthErrorMessage
import ru.kotlinschool.bot.ui.residentsErrorMessage
import ru.kotlinschool.bot.ui.yearError
import ru.kotlinschool.data.PublicServiceData
import ru.kotlinschool.exception.ParserException
import ru.kotlinschool.exception.YearNotSupportedException
import ru.kotlinschool.exception.validate
import java.math.BigDecimal
import java.time.Year

private const val FLAT_REQUIRED_LINES = 3
private const val SPACE_CHAR_DELIMETER = ' '
private const val LINE_DELIMETER = '-'
private val LINE_SEPARATOR = System.lineSeparator()

/**
 * Парсер данных квартиры
 *
 * @param text — Текст сообщения пользователя
 * @return распаршенные данные
 * @see UserActionsHandler.handleTextAction
 * @see FlatRegistrationData
 *
 * @throws ParserException в случае не соответствия введённых пользоветелем данных формату.
 */
@Throws(ParserException::class)
fun parseFlatData(text: String): FlatRegistrationData {
    val lines = text.split(LINE_SEPARATOR)
        .filter(String::isNotBlank)

    return if (lines.size == FLAT_REQUIRED_LINES) {
        // flat
        val flatNumTokens = lines.first().split(SPACE_CHAR_DELIMETER)

        val flatNum = when (flatNumTokens.size) {
            1 -> flatNumTokens.first()
            2 -> flatNumTokens[1]
            else -> throw ParserException(flatNumErrorMessage)
        }
        validate(flatNum, flatNumErrorMessage) { it.matches("""\d+|[А-Я]""".toRegex()).not() }

        // area
        val areaTokens = lines[1].split(SPACE_CHAR_DELIMETER)
        val area = when (areaTokens.size) {
            1 -> areaTokens.first()
            2 -> areaTokens[1]
            else -> throw ParserException(flatAreaErrorMessage)
        }.toDoubleOrNull() ?: throw ParserException(flatAreaErrorMessage)
        validate(area, flatAreaErrorMessage) { it > 0.0 && it < 1_000.0 }

        // residents
        val residentsTokens = lines[2].split(SPACE_CHAR_DELIMETER)
        val residentsNum = when (residentsTokens.size) {
            1 -> residentsTokens.first()
            2 -> residentsTokens[1]
            else -> throw ParserException(residentsErrorMessage)
        }.toLongOrNull() ?: throw ParserException(residentsErrorMessage)
        validate(area, residentsErrorMessage) { it > 0 && (area / residentsNum) >= 18 }

        FlatRegistrationData(flatNum, area, residentsNum)
    } else throw ParserException(formatErrorMessage)
}

/**
 * Парсер данных счётчиков
 *
 * @param text — текст сообщения пользователя
 * @return возвращает данные счётчиков, размапленные по id услуги

 * @see UserActionsHandler.handleMeterReadingsUpdate
 * @see MappedRegistrationData
 *
 * @throws ParserException в случае не соответствия введённых пользоветелем данных формату.
 */
@Throws(ParserException::class)
fun parseMeterReadings(text: String, publicServices: List<PublicServiceData>): List<MappedRegistrationData> {
    val lines = text.split(LINE_SEPARATOR)
        .filter(String::isNotBlank)

    return if (lines.size == publicServices.size) {
        lines.mapIndexed { index, line ->
            val tokens = line.split(SPACE_CHAR_DELIMETER)

            val value = when (tokens.size) {
                1 -> tokens.first()
                2 -> tokens[1]
                else -> throw ParserException(formatErrorMessage)
            }.toDoubleOrNull() ?: throw ParserException(formatErrorMessage)
            validate(value, formatErrorMessage) { it > 0 }

            MappedRegistrationData(publicServices[index].id, value)
        }
    } else throw ParserException(formatErrorMessage)
}

/**
 * Парсер тарифов
 *
 * @param text — текст сообщения админа
 * @return возвращает тарифы, размапленные по id услуги
 *
 * @see AdminActionsHandler.handleUpdateRates
 * @see MappedRatesData
 *
 * @throws ParserException в случае не соответствия введённых пользоветелем данных формату.
 */
@Throws(ParserException::class)
fun parseRates(text: String, publicServices: List<PublicServiceData>): List<MappedRatesData> {
    val lines = text.split(LINE_SEPARATOR)
        .filter(String::isNotBlank)

    return if (lines.size == publicServices.size) {
        lines.mapIndexed { index, line ->
            val tokens = line.split(SPACE_CHAR_DELIMETER)

            val value = when (tokens.size) {
                1 -> tokens.first().toBigDecimalOrNull() ?: throw ParserException(formatErrorMessage)
                2 -> tokens[1].toBigDecimalOrNull() ?: throw ParserException(formatErrorMessage)
                else -> throw ParserException(formatErrorMessage)
            }
            validate(value, formatErrorMessage) { it > BigDecimal.ZERO && it < BigDecimal.valueOf(10_000) }

            MappedRatesData(publicServices[index].id, value)
        }
    } else throw ParserException(formatErrorMessage)
}

@Throws(ParserException::class)
fun parseMonthMessageGetNumber(text: String): Int {
    return Integer
        .valueOf(text.split(LINE_DELIMETER)[1].trim())
        .also { month ->
            validate(month, monthErrorMessage) { it in 1..12 }
        }
}

/**
 * Парсер для значений года
 *
 * @throws YearNotSupportedException в случае когда выбран не поддерживаемый код
 */
@Throws(ParserException::class, YearNotSupportedException::class)
fun parseYear(text: String): Int {
    val year = text.toIntOrNull() ?: throw ParserException(yearError)
    if (year != Year.now().value)
        throw YearNotSupportedException()

    return year
}
