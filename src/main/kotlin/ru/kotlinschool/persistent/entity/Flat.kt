package ru.kotlinschool.persistent.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotNull

/**
 * Данные по квартире
 */
@Entity
data class Flat(
    /**
     * Ид пользователя-администратора в Telegram
     */
    @NotNull
    val userId: Long,

    /**
     * Дом
     */
    @ManyToOne
    @JoinColumn(name = "house_id")
    @NotNull
    val house: House,

    /**
     * Номер квартиры/офиса
     */
    @Column(name = "flat_number")
    @NotNull
    val number: String,

    /**
     * Площадь
     */
    var area: Double? = null,

    /**
     * Количество прописанных
     */
    var numberOfResidents: Int? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    /**
     * Услуги
     */
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "flat")
    val meterReadings: List<MeterReading> = ArrayList()
)
