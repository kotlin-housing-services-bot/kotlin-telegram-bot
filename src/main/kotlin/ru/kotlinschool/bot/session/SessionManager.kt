package ru.kotlinschool.bot.session

import org.springframework.stereotype.Component
import ru.kotlinschool.bot.handlers.model.SessionAwareRequest
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory менеджер сессий Telegram-пользователей
 *
 * @see SessionAwareRequest
 */
@Component
class SessionManager {

    private val sessionAwareRequestMap = ConcurrentHashMap<Long, SessionAwareRequest>()

    fun startSession(userId: Long, sessionType: SessionAwareRequest) {
        sessionAwareRequestMap[userId] = sessionType
    }

    fun getUserSession(userId: Long) =
        sessionAwareRequestMap[userId]

    fun resetUserSession(userId: Long) {
        sessionAwareRequestMap.remove(userId)
    }
}
