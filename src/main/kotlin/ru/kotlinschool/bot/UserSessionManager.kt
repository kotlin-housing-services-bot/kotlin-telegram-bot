package ru.kotlinschool.bot

import org.springframework.stereotype.Component
import ru.kotlinschool.bot.handlers.entities.UserSession

/**
 * In-memory менеджер сессий Telegram-пользователей
 *
 * @see UserSession
 */
@Component
class UserSessionManager {

    private val userSessionMap = HashMap<Long, UserSession>()

    fun startSession(userId: Long, sessionType: UserSession) {
        userSessionMap[userId] = sessionType
    }

    fun getUserSession(userId: Long) =
        userSessionMap[userId]

    fun resetUserSession(userId: Long) {
        userSessionMap.remove(userId)
    }
}
