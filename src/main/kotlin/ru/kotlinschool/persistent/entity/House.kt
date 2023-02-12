package ru.kotlinschool.persistent.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotNull

/**
 * Дом, обслуживаемый УК
 */
@Entity
data class House(
    /**
     * Управляющая компания
     */
    @ManyToOne
    @JoinColumn(name = "management_company_id")
    @NotNull
    val managementCompany: ManagementCompany,

    /**
     * Адрес
     */
    @NotNull
    val address: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    /**
     * Квартиры
     */
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "house")
    val flats: List<Flat> = ArrayList(),

    /**
     * Услуги
     */
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "house")
    val publicServices: List<PublicService> = ArrayList()
)
