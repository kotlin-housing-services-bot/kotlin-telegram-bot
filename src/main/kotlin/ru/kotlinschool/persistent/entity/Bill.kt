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
data class Bill(
    /**
     * Квартира
     */
    @ManyToOne
    @JoinColumn(name = "flat_id")
    @NotNull
    val flat: Flat,

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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Bill

        if (flat != other.flat) return false
        if (month != other.month) return false
        if (!billData.contentEquals(other.billData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = flat.hashCode()
        result = 31 * result + month
        result = 31 * result + billData.contentHashCode()
        return result
    }
}

