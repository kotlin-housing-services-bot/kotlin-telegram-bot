package ru.kotlinschool.persistent.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotNull

/**
 * Данные управляющей компании
 */
@Entity
class ManagementCompany (
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
