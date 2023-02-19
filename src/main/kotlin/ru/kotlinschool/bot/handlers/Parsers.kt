package ru.kotlinschool.bot.handlers

import ru.kotlinschool.bot.handlers.entities.FlatRegistrationData
import ru.kotlinschool.bot.handlers.entities.MappedRatesData
import ru.kotlinschool.bot.handlers.entities.MappedRegistrationData
import ru.kotlinschool.bot.ui.flatAreaErrorMessage
import ru.kotlinschool.bot.ui.flatNumErrorMessage
import ru.kotlinschool.bot.ui.formatErrorMessage
import ru.kotlinschool.bot.ui.houseIdErrorMessage
import ru.kotlinschool.bot.ui.residentsErrorMessage
import ru.kotlinschool.data.PublicServiceData
import ru.kotlinschool.exception.ParserException
import java.math.BigDecimal

private const val FLAT_REQUIRED_LINES = 4
private const val SPACE_CHAR_DELIMETER = ' '
private const val SEMICOLON_DELIMETER = ";"
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
        .filter { it.isNotBlank() }

    return if (lines.size == FLAT_REQUIRED_LINES) {
        // house id
        val houseIdTokens = lines.first().split(SPACE_CHAR_DELIMETER)
        val houseId = when (houseIdTokens.size) {
            1 -> houseIdTokens.first()
            2 -> houseIdTokens[1]
            else -> throw ParserException(houseIdErrorMessage)
        }.toLongOrNull() ?: throw ParserException(houseIdErrorMessage)

        // flat
        val flatNumTokens = lines[1].split(SPACE_CHAR_DELIMETER)

        val flatNum = when (flatNumTokens.size) {
            1 -> flatNumTokens.first()
            2 -> flatNumTokens[1]
            else -> throw ParserException(flatNumErrorMessage)
        }

        // area
        val areaTokens = lines[2].split(SPACE_CHAR_DELIMETER)
        val area = when (areaTokens.size) {
            1 -> areaTokens.first()
            2 -> areaTokens[1]
            else -> throw ParserException(flatAreaErrorMessage)
        }.toDoubleOrNull() ?: throw ParserException(flatAreaErrorMessage)

        // residents
        val residentsTokens = lines[3].split(SPACE_CHAR_DELIMETER)
        val residentsNum = when (residentsTokens.size) {
            1 -> residentsTokens.first()
            2 -> residentsTokens[1]
            else -> throw ParserException(residentsErrorMessage)
        }.toLongOrNull() ?: throw ParserException(residentsErrorMessage)

        FlatRegistrationData(houseId, flatNum, area, residentsNum)
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
        .filter { it.isNotBlank() }

    return if (lines.size == publicServices.size) {
        lines.mapIndexed { index, line ->
            val tokens = line.split(SPACE_CHAR_DELIMETER)

            val value = when (tokens.size) {
                1 -> tokens.first()
                2 -> tokens[1]
                else -> throw ParserException(formatErrorMessage)
            }.toDoubleOrNull() ?: throw ParserException(formatErrorMessage)

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
        .filter { it.isNotBlank() }

    return if (lines.size == publicServices.size) {
        lines.mapIndexed { index, line ->
            val tokens = line.split(SPACE_CHAR_DELIMETER)

            val value = when (tokens.size) {
                1 -> tokens.first().extractRateValues()
                2 -> tokens[1].extractRateValues()
                else -> throw ParserException(formatErrorMessage)
            }

            MappedRatesData(publicServices[index].id, value)
        }
    } else throw ParserException(formatErrorMessage)
}

// TODO
private fun String.extractRateValues(): BigDecimal =
    split(SEMICOLON_DELIMETER).takeIf { it.size == 2 }
        ?.let { (valueString) ->
            val value = valueString.toBigDecimalOrNull() ?: throw ParserException(formatErrorMessage)

            value
        } ?: throw ParserException(formatErrorMessage)
