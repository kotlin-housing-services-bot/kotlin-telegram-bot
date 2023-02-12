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

/**
 * Коммунальные услуги
 */
@Entity
data class MeterReading(
    /**
     * Квартира
     */
    @ManyToOne
    @JoinColumn(name = "flat_id")
    @NotNull
    val flat: Flat,

    /**
     * Услуга
     */
    @ManyToOne
    @JoinColumn(name = "public_service_id")
    @NotNull
    val publicService: PublicService,

    /**
     * Значение
     */
    @NotNull
    @Column(name = "meter_reading_value")
    val value: Double,

    /**
     * Дата передачи показания
     */
    @NotNull
    @Column(name = "meter_reading_date")
    val actionDate: LocalDate,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
)
