package ru.kotlinschool.bot.handlers

import org.springframework.beans.factory.annotation.Autowired
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
import ru.kotlinschool.bot.ui.*
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
class AdminActionsHandler @Autowired constructor(
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

        val response = if (sessionType == null) {
            handleTextAction(message)
        } else {
            handleActionWithSession(message, sessionType)
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
            Command.Admin.UpdateRates.commandText -> {
                handleUpdateRatesWithoutSession(message)
            }

            Command.Admin.TriggerCalculations.commandText -> {
                handleTriggerCalculationWithoutSession(message)
            }

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
    private fun handleUpdateRatesWithSession(message: Message, userSession: UpdateRatesRequest): List<SendMessage> =
        when (userSession) {
            is UpdateRatesRequest.SelectHouseRequest -> {
                val selectedHouse = userSession.houses.first { it.address == message.text }
                val services = adminService.getPublicServices(selectedHouse.id)
                    .sortedBy { it.id }

                sessionManager.startSession(message.from.id, UpdateRatesRequest.UpdateRequest(services))

                createRatesUpdateMessages(message.chatId, services)
            }

            is UpdateRatesRequest.UpdateRequest -> {
                parseRates(message.text, userSession.publicServices).forEach { (id, value) ->
                    adminService.setRate(id, value)
                }

                sessionManager.resetUserSession(message.from.id)

                listOf(buildAnswerMessage(message.chatId, dataSavedMessage))
            }
        }

    private fun handleTriggerCalculationWithoutSession(message: Message): HandlerResponse.Broadcast {

        val messageIdLong = message.chatId
        val messagesToAdmin: List<SendMessage> = listOf(
            buildAnswerMessage(messageIdLong, billsSentMessage)
        )

        val broadcastToUsers: List<PartialBotApiMethod<out Serializable>> = adminService
            .getHouses(messageIdLong)
            .flatMap { adminService.calculateBills(it.id) }
            .flatMap {
                val userIdStr = it.userId.toString()
                listOf(
                    SendMessage().apply {
                        chatId = userIdStr
                        text = newPaymentBill
                    },
                    SendDocument().apply {
                        chatId = userIdStr
                        document = InputFile(ByteArrayInputStream(it.data), it.fileName)
                    }
                )
            }

        return HandlerResponse.Broadcast(messagesToAdmin, broadcastToUsers)
    }
}
