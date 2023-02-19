package ru.kotlinschool.bot

import org.springframework.stereotype.Component
import ru.kotlinschool.bot.handlers.entities.SessionAwareRequest

/**
 * In-memory менеджер сессий Telegram-пользователей
 *
 * @see SessionAwareRequest
 */
@Component
class SessionManager {

    private val sessionAwareRequestMap = HashMap<Long, SessionAwareRequest>()

    fun startSession(userId: Long, sessionType: SessionAwareRequest) {
        sessionAwareRequestMap[userId] = sessionType
    }

    fun getUserSession(userId: Long) =
        sessionAwareRequestMap[userId]

    fun resetUserSession(userId: Long) {
        sessionAwareRequestMap.remove(userId)
    }
}
