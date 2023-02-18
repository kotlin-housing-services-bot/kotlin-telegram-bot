package ru.kotlinschool.bot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import ru.kotlinschool.bot.handlers.AdminActionsHandler
import ru.kotlinschool.bot.handlers.UserActionsHandler
import ru.kotlinschool.bot.handlers.entities.HandlerResponse
import ru.kotlinschool.bot.ui.CLEARED_KEYBOARD
import ru.kotlinschool.bot.ui.Command
import ru.kotlinschool.bot.ui.START_KEYBOARD_ADMIN
import ru.kotlinschool.bot.ui.START_KEYBOARD_USER
import ru.kotlinschool.bot.ui.commandNotSupportedErrorMessage
import ru.kotlinschool.bot.ui.farewellMessage
import ru.kotlinschool.bot.ui.welcomeMessage

/**
 * Бот для обработки действия пользователя. Входная точка в приложение.
 * Рулит входящими, исходящими сообщениями и клавиатурой.
 *
 * @see UserSessionManager
 */
@Component
class HousingBot @Autowired constructor(
    private val userActionsHandler: UserActionsHandler,
    private val adminActionsHandler: AdminActionsHandler,
    private val userSessionManager: UserSessionManager,
) : TelegramLongPollingBot("6105576274:AAEBjUYM_paN095NjTMJXyTHQtIrkwVkYgo") {

    private val botName: String = "УК Умный Дом"

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update) {
        if (update.message != null) {
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
        when (message.text) {
            Command.Start.commandText -> {
                SendMessage().apply {
                    chatId = message.chatId.toString()
                    text = welcomeMessage
                    replyMarkup = if (isAdmin) START_KEYBOARD_ADMIN else START_KEYBOARD_USER
                }.let(::execute)
            }
            Command.Stop.commandText -> {
                userSessionManager.resetUserSession(message.from.id)

                SendMessage().apply {
                    chatId = message.chatId.toString()
                    text = farewellMessage
                    replyMarkup = CLEARED_KEYBOARD
                }.let(::execute)
            }
            else -> SendMessage().apply {
                chatId = message.chatId.toString()
                text = commandNotSupportedErrorMessage
            }.let(::execute)
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
     * @see AdminActionsHandler — обработка текстовых действий для админа
     * @see UserActionsHandler — обработка текстовых действий для обычного пользователя
     */
    private fun handleTextAction(message: Message, isAdmin: Boolean = false) {
        val messageText = message.text

        if (messageText.contains(Command.Cancel.commandText)) {
            userSessionManager.resetUserSession(message.from.id)

            SendMessage().apply {
                chatId = message.chatId.toString()
                text = farewellMessage
                replyMarkup = CLEARED_KEYBOARD
            }.let(::execute)

        } else {
            if (isAdmin) {
                adminActionsHandler.handle(message) { response ->
                    when (response) {
                        is HandlerResponse.Basic -> response.messages.forEach(::execute)

                        is HandlerResponse.Broadcast -> response.run {
                            broadcastMessages.forEach { (message, document) ->
                                execute(message)
                                execute(document)
                            }
                            response.messages.forEach(::execute)
                        }
                    }
                }
            } else {
                userActionsHandler.handle(message) { response ->
                    if (response is HandlerResponse.Basic) {
                        response.messages.forEach(::execute)
                    }
                }
            }
        }
    }
}
