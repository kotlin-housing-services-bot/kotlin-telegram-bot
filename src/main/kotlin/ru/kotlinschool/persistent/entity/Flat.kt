package ru.kotlinschool.persistent.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotNull

/**
 * Данные по квартире
 */
@Entity
class Flat(
    /**
     * Ид пользователя-администратора в Telegram
     */
    @NotNull
    val userId: Long,


    /**
     * Ид чата в Telegram
     */
    @NotNull
    val chatId: Long,

    /**
     * Дом
     */
    @ManyToOne(fetch = FetchType.EAGER)
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
    var area: Double,

    /**
     * Количество прописанных
     */
    var numberOfResidents: Long,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    /**
     * Услуги
     */
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "flat", fetch = FetchType.EAGER)
    val metrics: List<Metric> = ArrayList()
)
