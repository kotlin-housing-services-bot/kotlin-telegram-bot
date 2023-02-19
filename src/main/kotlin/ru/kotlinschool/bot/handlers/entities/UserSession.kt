package ru.kotlinschool.bot.handlers.entities

import ru.kotlinschool.data.FlatData
import ru.kotlinschool.data.HouseData
import ru.kotlinschool.data.PublicServiceData

/**
 * Класс для stateful взаимодействий с ботом, то есть когда требуется отслеживать сессию пользователя
 */
sealed class UserSession {
    object FlatRegistration : UserSession()
}

sealed class UpdateRates : UserSession() {

    data class SelectHouse(val houses: List<HouseData>) : UpdateRates()

    data class Update(val publicServices: List<PublicServiceData>) : UpdateRates()
}

sealed class PreviousBill : UserSession() {

    object StartRequest : PreviousBill()

    data class SelectFlat(val flats: List<FlatData>) : PreviousBill()

    data class SelectMonth(val flat: FlatData) : PreviousBill()
}

sealed class MeterReadingsAdd : UserSession() {

    data class SelectFlat(val flats: List<FlatData>) : MeterReadingsAdd()

    data class Add(val flat: FlatData, val publicServices: List<PublicServiceData>) : MeterReadingsAdd()
}
