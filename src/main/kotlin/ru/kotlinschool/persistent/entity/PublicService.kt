package ru.kotlinschool.persistent.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotNull

/**
 * Коммунальные услуги
 */
@Entity
class PublicService(
    /**
     * Дом
     */
    @ManyToOne
    @JoinColumn(name = "house_id")
    @NotNull
    val house: House,

    /**
     * Название услуги
     */
    @Column(name = "public_service_name")
    @NotNull
    val name: String,

    /**
     * Тип расчета
     *
     */
    @NotNull
    @Enumerated(value = EnumType.STRING)
    val calculationType: CalculationType,

    /**
     * Единица измерения
     *
     */
    @NotNull
    val unit: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    /**
     * Тарифы
     */
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "publicService", fetch = FetchType.EAGER)
    val rates: List<Rate> = ArrayList()
)

/**
 * Тип расчета суммы
 */
enum class CalculationType {
    BY_MONTHLY_RATE,
    BY_METER,
    BY_FLAT_AREA,
    BY_NUMBER_OF_RESIDENTS
}
