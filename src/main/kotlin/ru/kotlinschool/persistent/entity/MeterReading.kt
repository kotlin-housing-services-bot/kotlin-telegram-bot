package ru.kotlinschool.persistent.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.io.Serializable
import java.time.LocalDate

/**
 * Коммунальные услуги
 */
@Entity
data class MeterReading(
    /**
     * Дом
     */
    @ManyToOne
    @JoinColumn(name = "flat_id")
    @NotNull
    val flat: Flat,

    /**
     * Услуга
     */
    @ManyToOne
    @JoinColumn(name = "service_id")
    @NotNull
    val service: PublicService,

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
