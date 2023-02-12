package ru.kotlinschool.persistent.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

@Entity
data class Rate(
    /**
     * Услуга
     */
    @ManyToOne
    @JoinColumn(name = "public_service_id")
    @NotNull
    val publicService: PublicService,

    /**
     * Тариф
     */
    @Column(name = "rate_sum")
    @NotNull
    val sum: Double,

    /**
     * Дата начала действия тарифа
     */
    @NotNull
    val dateBegin: LocalDate,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
)

