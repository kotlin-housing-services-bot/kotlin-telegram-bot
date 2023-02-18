package ru.kotlinschool.bot.handlers.entities

import ru.kotlinschool.dto.FlatDto
import ru.kotlinschool.dto.HouseDto
import ru.kotlinschool.dto.PublicServiceDto

/**
 * Класс для stateful взаимодействий с ботом, то есть когда требуется отслеживать сессию пользователя
 */
sealed class UserSession {
    object FlatRegistration : UserSession()
}

sealed class UpdateRates : UserSession() {

    data class SelectHouse(val houses: List<HouseDto>) : UpdateRates()

    data class Update(val publicServices: List<PublicServiceDto>) : UpdateRates()
}

sealed class PreviousBill : UserSession() {

    object StartRequest : PreviousBill()

    data class SelectFlat(val flats: List<FlatDto>) : PreviousBill()

    data class SelectMonth(val flat: FlatDto) : PreviousBill()
}

sealed class MeterReadingsAdd : UserSession() {

    data class SelectFlat(val flats: List<FlatDto>) : MeterReadingsAdd()

    data class Add(val flat: FlatDto, val publicServices: List<PublicServiceDto>) : MeterReadingsAdd()
}
