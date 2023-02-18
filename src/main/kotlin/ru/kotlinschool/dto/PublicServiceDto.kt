package ru.kotlinschool.dto

import ru.kotlinschool.persistent.entity.CalculationType

data class PublicServiceDto(
    val id: Long,
    val name: String,
    val calculationType: CalculationType,
)
