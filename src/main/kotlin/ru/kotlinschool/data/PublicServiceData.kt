package ru.kotlinschool.data

import ru.kotlinschool.persistent.entity.CalculationType

data class PublicServiceData(
    val id: Long,
    val name: String,
    val calculationType: CalculationType,
)
