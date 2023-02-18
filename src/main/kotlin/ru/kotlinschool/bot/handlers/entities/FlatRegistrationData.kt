package ru.kotlinschool.bot.handlers.entities

data class FlatRegistrationData(
    val houseId: Long,
    val flatNum: String,
    val area: Double,
    val residentsNum: Long,
)
