package ru.kotlinschool.bot

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import ru.kotlinschool.bot.handlers.AdminActionsHandler
import ru.kotlinschool.bot.handlers.UserActionsHandler
import ru.kotlinschool.bot.handlers.model.HandlerResponse
import ru.kotlinschool.bot.session.SessionManager
import ru.kotlinschool.bot.ui.CLEARED_KEYBOARD
import ru.kotlinschool.bot.ui.Command
import ru.kotlinschool.bot.ui.START_KEYBOARD_ADMIN
import ru.kotlinschool.bot.ui.START_KEYBOARD_USER
import ru.kotlinschool.bot.ui.commandNotSupportedErrorMessage
import ru.kotlinschool.bot.ui.farewellMessage
import ru.kotlinschool.bot.ui.welcomeMessage
import ru.kotlinschool.util.BotApiMethod
import ru.kotlinschool.util.buildAnswerMessage

/**
 * Бот для обработки действия пользователя. Входная точка в приложение.
 * Рулит входящими, исходящими сообщениями и клавиатурой.
 *
 * @see SessionManager
 */
@Component
class HousingBot(
    private val userActionsHandler: UserActionsHandler,
    private val adminActionsHandler: AdminActionsHandler,
    private val sessionManager: SessionManager,
    @Value("\${telegram.bot.token}") botToken: String
) : TelegramLongPollingBot(botToken) {

    override fun getBotUsername(): String = "УК Умный Дом"

    override fun onUpdateReceived(update: Update) {
        if (update.message != null && update.message.from != null) {
            val isAdmin = adminActionsHandler.checkAdmin(update.message.from.id)

            if (update.message.isCommand) {
                handleCommand(update.message, isAdmin)
            } else {
                handleTextAction(update.message, isAdmin)
            }
        }
    }

    /**
     * Обработка команд к боту
     * 1. Показ стартового меню
     * 2. Завершение работы
     *
     * @param message — Входное сообщение
     * @param isAdmin — Админ-пользователь
     *
     * @see handleTextAction — обработка основных действий
     */
    private fun handleCommand(message: Message, isAdmin: Boolean) {
        val chatId = message.chatId
        when (message.text) {
            Command.Start.commandText -> {
                // reset old sessions on start
                sessionManager.resetUserSession(message.from.id)

                val keyboard = if (isAdmin) START_KEYBOARD_ADMIN else START_KEYBOARD_USER
                buildAnswerMessage(chatId, welcomeMessage, keyboard).let(::execute)
            }
            Command.Stop.commandText -> {
                sessionManager.resetUserSession(message.from.id)

                buildAnswerMessage(chatId, farewellMessage, CLEARED_KEYBOARD).let(::execute)
            }
            else -> buildAnswerMessage(chatId, commandNotSupportedErrorMessage).let(::execute)
        }
    }

    /**
     * Обработка основных действий в текстовом формате
     * 1. Сброс текущей сессии пользователя
     * 2. Контекстные действия в рамках пользовательской сессии
     * 3. Делегация обработки текстовых действий пользователя или админа
     *
     * @param message — Входное сообщение
     * @param isAdmin — Админ-пользователь
     *
     */
    private fun handleTextAction(message: Message, isAdmin: Boolean = false) {
        if (message.text.contains(Command.Cancel.commandText)) {
            sessionManager.resetUserSession(message.from.id)

            buildAnswerMessage(message.chatId, farewellMessage, CLEARED_KEYBOARD).let(::execute)
        } else if (isAdmin) {
            handleTextActionAdmin(message)
        } else {
            handleTextMessageUser(message)
        }
    }

    /**
     * Обработка основных действий админа в текстовом формате
     *
     * @param message — Входное сообщение
     * @see AdminActionsHandler — обработка текстовых действий для админа
     */
    private fun handleTextActionAdmin(message: Message) {
        adminActionsHandler.handle(message) { response ->
            when (response) {
                is HandlerResponse.Basic -> response.messages.forEach(::sendResponse)
                is HandlerResponse.Broadcast -> response.run {
                    broadcastMessagesToUsers.forEach(::sendResponse)
                    response.messagesToAdmin.forEach(::execute)
                }
            }
        }
    }

    /**
     * Обработка основных действий пользователя в текстовом формате
     *
     * @param message — Входное сообщение
     * @see UserActionsHandler — обработка текстовых действий для обычного пользователя
     */
    private fun handleTextMessageUser(message: Message) {
        userActionsHandler.handle(message) { response ->
            if (response is HandlerResponse.Basic) {
                response.messages.forEach(::sendResponse)
            }
        }
    }

    private fun sendResponse(apiMethod: BotApiMethod) {
        when (apiMethod) {
            is SendMessage -> execute(apiMethod)
            is SendDocument -> execute(apiMethod)
        }
    }
}
