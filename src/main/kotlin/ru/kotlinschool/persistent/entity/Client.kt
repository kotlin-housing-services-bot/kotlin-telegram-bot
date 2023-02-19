package ru.kotlinschool.persistent.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotNull

@Entity
class Client(

    /**
     * Ид чата администратора в Telegram
     */
    @NotNull
    val telegramChatId: Long,

    /**
     * Ид пользователя в Telegram
     */
    @NotNull
    val telegramUserId: Long,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
)
