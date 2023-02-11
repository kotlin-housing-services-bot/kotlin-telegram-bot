package ru.kotlinschool.persistent.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.io.Serializable

/**
 * Данные управляющей компании
 */
@Entity
data class ManagementCompany (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    /**
     * Название организации
     */
    @Column(name = "company_name")
    @NotNull
    val name: String,

    /**
     * ИНН
     */
    @NotNull
    val inn: String,

    /**
     * Ид пользователя-администратора в Telegram
     */
    @NotNull
    val userId: Long,

    /**
     * Дома
     */
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "managementCompany")
    var houses: List<House> = ArrayList()
) : Serializable {

    override fun toString(): String {
        return "УК (name: $name, инн: $inn)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is ManagementCompany)
            return false
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + inn.hashCode()
        return result
    }
}
