package ru.kotlinschool.persistent.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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
data class PublicService(
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    /**
     * Тарифы
     */
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "publicService")
    val rates: List<Rate> = ArrayList()
)

/**
 * Тип расчета суммы
 */
enum class CalculationType(val description: String) {
    BY_MONTHLY_RATE("ежемесячному тарифу"),
    BY_METER("по показаниям счетчика"),
    BY_FLAT_AREA("по площади квартиры"),
    BY_NUMBER_OF_RESDENTS("по количеству прописанных")
}
