package ru.kotlinschool.bot.handlers

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import ru.kotlinschool.bot.handlers.model.HandlerResponse
import ru.kotlinschool.bot.handlers.model.SessionAwareRequest
import ru.kotlinschool.bot.handlers.model.UpdateRatesRequest
import ru.kotlinschool.bot.session.SessionManager
import ru.kotlinschool.bot.ui.CANCEL_KEYBOARD
import ru.kotlinschool.bot.ui.Command
import ru.kotlinschool.bot.ui.START_KEYBOARD_USER
import ru.kotlinschool.bot.ui.addMeterReadingNotification
import ru.kotlinschool.bot.ui.billsSentMessage
import ru.kotlinschool.bot.ui.commandNotSupportedErrorMessage
import ru.kotlinschool.bot.ui.createHousesKeyboard
import ru.kotlinschool.bot.ui.dataSavedMessage
import ru.kotlinschool.bot.ui.newPaymentBill
import ru.kotlinschool.bot.ui.notificationsSentMessage
import ru.kotlinschool.bot.ui.retryMessage
import ru.kotlinschool.bot.ui.selectHouseMessage
import ru.kotlinschool.bot.ui.unknownError
import ru.kotlinschool.exception.EntityNotFoundException
import ru.kotlinschool.exception.HouseNotRegisteredException
import ru.kotlinschool.exception.ParserException
import ru.kotlinschool.service.AdminService
import ru.kotlinschool.util.ResponseCallback
import ru.kotlinschool.util.buildAnswerMessage
import ru.kotlinschool.util.createRatesUpdateMessages
import ru.kotlinschool.util.parseRates
import java.io.ByteArrayInputStream
import java.io.Serializable

/**
 * Обработчик команд от админ-пользователя.
 *
 * @see SessionManager
 */
@Component
class AdminActionsHandler(
    private val adminService: AdminService,
    private val sessionManager: SessionManager,
) {

    /**
     * Основная точка входа. Разводит обработку команд на зависимые от текущей сессии пользователя и обычные.
     *
     * @see handleTextAction
     * @see handleActionWithSession
     *
     * @see ResponseCallback
     */
    fun handle(message: Message, callback: ResponseCallback) {
        val sessionType = sessionManager.getUserSession(message.from.id)

        val response = runCatching {
            if (sessionType == null) {
                handleTextAction(message)
            } else {
                handleActionWithSession(message, sessionType)
            }
        }.getOrElse { error ->
            error.printStackTrace()
            val chatId = message.chatId
            val messages = when (error) {
                is EntityNotFoundException -> listOf(
                    buildAnswerMessage(chatId, error.message),
                    buildAnswerMessage(chatId, retryMessage, CANCEL_KEYBOARD)
                )
                is ParserException -> listOf(
                    buildAnswerMessage(chatId, error.message.orEmpty()),
                    buildAnswerMessage(chatId, retryMessage, CANCEL_KEYBOARD)
                )
                is HouseNotRegisteredException -> {
                    sessionManager.resetUserSession(message.from.id)
                    listOf(
                        buildAnswerMessage(chatId, error.message.orEmpty()),
                        buildAnswerMessage(chatId, retryMessage, START_KEYBOARD_USER)
                    )
                }
                else -> listOf(
                    buildAnswerMessage(chatId, unknownError),
                    buildAnswerMessage(chatId, retryMessage, CANCEL_KEYBOARD)
                )
            }

            HandlerResponse.Basic(messages)
        }
        callback(response)
    }

    fun checkAdmin(userId: Long) = adminService.isAdmin(userId)

    /**
     * Обработчик команд, которые не зависят от текущей сессии.
     * Преимущественно стартует новую сессию для пользователя.
     *
     * @param message — Входное сообщение
     * @return Ответные сообщения по результатам обработки
     *
     * @see handleActionWithSession
     *
     * @see SendMessage
     * @see HandlerResponse
     */
    private fun handleTextAction(message: Message): HandlerResponse =
        when (message.text) {
            Command.Admin.UpdateRates.commandText -> handleUpdateRatesWithoutSession(message)

            Command.Admin.TriggerCalculations.commandText -> handleTriggerCalculationWithoutSession(message)

            Command.Admin.TriggerNotify.commandText -> handleNotificationWithoutSession(message)

            else -> HandlerResponse.Basic(listOf(buildAnswerMessage(message.chatId, commandNotSupportedErrorMessage)))
        }

    /**
     * Обработчик команд пользователя в рамках некоторой сессии.
     *
     * @param message — Входное сообщение
     * @return Ответные сообщения по результатам обработки
     *
     * @see SessionAwareRequest
     */
    private fun handleActionWithSession(message: Message, sessionAwareRequest: SessionAwareRequest): HandlerResponse {
        val messages = when (sessionAwareRequest) {
            is UpdateRatesRequest -> handleUpdateRatesWithSession(message, sessionAwareRequest)
            else -> listOf(buildAnswerMessage(message.chatId, commandNotSupportedErrorMessage))
        }
        return HandlerResponse.Basic(messages)
    }

    private fun handleUpdateRatesWithoutSession(message: Message): HandlerResponse.Basic {
        val houses = adminService.getHouses(message.from.id)
        sessionManager.startSession(message.from.id, UpdateRatesRequest.SelectHouseRequest(houses))

        return HandlerResponse.Basic(
            listOf(buildAnswerMessage(message.chatId, selectHouseMessage, createHousesKeyboard(houses)))
        )
    }

    /**
     * Обработка сообщений на изменение тарифов в зависимости от актуально сессии [UpdateRatesRequest].
     *
     * @param message — Входное сообщение
     * @return Ответные сообщения по результатам обработки
     *
     * @see UpdateRatesRequest
     */
    private fun handleUpdateRatesWithSession(message: Message, userSession: UpdateRatesRequest): List<SendMessage> {
        val userId = message.from.id
        val chatId = message.chatId
        val text = message.text
        return when (userSession) {
            is UpdateRatesRequest.SelectHouseRequest -> {
                val selectedHouse = userSession.houses.first { it.address == text }
                val services = adminService.getPublicServices(selectedHouse.id).sortedBy { it.id }

                sessionManager.startSession(userId, UpdateRatesRequest.UpdateRequest(services))

                createRatesUpdateMessages(chatId, services)
            }

            is UpdateRatesRequest.UpdateRequest -> {
                parseRates(text, userSession.publicServices).forEach { (id, value) ->
                    adminService.setRate(id, value)
                }

                sessionManager.resetUserSession(userId)

                listOf(buildAnswerMessage(chatId, dataSavedMessage))
            }
        }
    }

    private fun handleTriggerCalculationWithoutSession(message: Message): HandlerResponse.Broadcast {
        val messageId: Long = message.chatId
        val messagesToAdmin: List<SendMessage> = listOf(buildAnswerMessage(messageId, billsSentMessage))

        val broadcastToUsers: List<PartialBotApiMethod<out Serializable>> = adminService
            .getHouses(messageId)
            .flatMap { adminService.calculateBills(it.id) }
            .flatMap {
                listOf(
                    buildAnswerMessage(it.chatId, newPaymentBill),
                    SendDocument().apply {
                        chatId = it.chatId.toString()
                        document = InputFile(ByteArrayInputStream(it.data), it.fileName)
                    }
                )
            }

        return HandlerResponse.Broadcast(messagesToAdmin, broadcastToUsers)
    }

    private fun handleNotificationWithoutSession(message: Message): HandlerResponse.Broadcast {
        val messageId: Long = message.chatId
        val messagesToAdmin: List<SendMessage> = listOf(buildAnswerMessage(messageId, notificationsSentMessage))

        val broadcastToUsers: List<PartialBotApiMethod<out Serializable>> = adminService
            .getHouses(messageId)
            .flatMap { adminService.getUsers(it.id) }
            .toSet()
            .map { buildAnswerMessage(it.id, addMeterReadingNotification) }

        return HandlerResponse.Broadcast(messagesToAdmin, broadcastToUsers)
    }
}
