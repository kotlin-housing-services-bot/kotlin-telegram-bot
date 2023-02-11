package ru.kotlinschool.persistent.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.io.Serializable

/**
 * Данные управляющей компании
 */
@Entity
data class ManagementCompany (
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    /**
     * Дома
     */
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "managementCompany")
    var houses: List<House> = ArrayList()
)
