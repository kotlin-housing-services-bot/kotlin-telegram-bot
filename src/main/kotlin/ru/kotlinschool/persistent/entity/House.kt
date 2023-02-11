package ru.kotlinschool.persistent.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.io.Serializable

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

    /**
     * Ид группы в Telegram
     */
    @NotNull
    val chatId: Long,

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
    val services: List<PublicService> = ArrayList()
)
