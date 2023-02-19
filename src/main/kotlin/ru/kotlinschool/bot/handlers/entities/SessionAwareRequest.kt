package ru.kotlinschool.bot.handlers.entities

import ru.kotlinschool.data.FlatData
import ru.kotlinschool.data.HouseData
import ru.kotlinschool.data.PublicServiceData

/**
 * Класс для stateful взаимодействий с ботом, то есть когда требуется отслеживать сессию пользователя
 */
sealed class SessionAwareRequest {
    object FlatRegistrationRequest : SessionAwareRequest()
}

sealed class UpdateRatesRequest : SessionAwareRequest() {

    data class SelectHouseRequest(val houses: List<HouseData>) : UpdateRatesRequest()

    data class UpdateRequest(val publicServices: List<PublicServiceData>) : UpdateRatesRequest()
}

sealed class PreviousBillRequest : SessionAwareRequest() {

    object StartRequest : PreviousBillRequest()

    data class SelectFlatRequest(val flats: List<FlatData>) : PreviousBillRequest()

    data class SelectMonthRequest(val flat: FlatData) : PreviousBillRequest()
}

sealed class AddMetricsRequest : SessionAwareRequest() {

    data class SelectFlatRequest(val flats: List<FlatData>) : AddMetricsRequest()

    data class AddRequest(val flat: FlatData, val publicServices: List<PublicServiceData>) : AddMetricsRequest()
}
