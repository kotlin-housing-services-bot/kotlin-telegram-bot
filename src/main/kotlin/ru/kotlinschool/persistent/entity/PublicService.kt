package ru.kotlinschool.persistent.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.io.Serializable
import java.time.LocalDate


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
    @Column(name = "service_name")
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
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "service")
    val rates: List<Rate> = ArrayList()
)


@Entity
data class Rate(
    /**
     * Услуга
     */
    @ManyToOne
    @JoinColumn(name = "service_id")
    @NotNull
    val service: PublicService,

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

/**
 * Тип расчета суммы
 */
enum class CalculationType(val description: String) {
    BY_MONTHLY_RATE("ежемесячному тарифу"),
    BY_METER("по показаниям счетчика"),
    BY_FLAT_AREA("по площади квартиры"),
    BY_NUMBER_OF_RESDENTS("по количеству прописанных")
}