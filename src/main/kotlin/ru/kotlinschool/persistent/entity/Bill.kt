package ru.kotlinschool.persistent.entity

import jakarta.persistence.Column
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Entity
import jakarta.persistence.Lob
import jakarta.validation.constraints.NotNull

@Entity
class Bill(
    /**
     * Квартира
     */
    @ManyToOne
    @JoinColumn(name = "flat_id")
    @NotNull
    val flat: Flat,

    /**
     * Год
     */
    @NotNull
    @Column(name = "bill_year")
    val year: Int,

    /**
     * Месяц
     */
    @NotNull
    @Column(name = "bill_month")
    val month: Int,

    /**
     * Квитанция
     */
    @Lob
    val billData: ByteArray,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
)
