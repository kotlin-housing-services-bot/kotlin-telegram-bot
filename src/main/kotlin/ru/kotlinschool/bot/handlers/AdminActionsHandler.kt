package ru.kotlinschool.bot.handlers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import ru.kotlinschool.bot.UserSessionManager
import ru.kotlinschool.bot.handlers.entities.BroadcastData
import ru.kotlinschool.bot.handlers.entities.HandlerResponse
import ru.kotlinschool.bot.handlers.entities.UpdateRates
import ru.kotlinschool.bot.handlers.entities.UserSession
import ru.kotlinschool.bot.ui.Command
import ru.kotlinschool.bot.ui.billsSentMessage
import ru.kotlinschool.bot.ui.commandNotSupportedErrorMessage
import ru.kotlinschool.bot.ui.createHousesKeyboard
import ru.kotlinschool.bot.ui.dataSavedMessage
import ru.kotlinschool.bot.ui.selectHouseMessage
import ru.kotlinschool.service.AdminService

/**
 * Обработчик команд от админ-пользователя.
 *
 * @see UserSessionManager
 */
@Component
class AdminActionsHandler @Autowired constructor(
    private val adminService: AdminService,
    private val userSessionManager: UserSessionManager,
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
        val sessionType = userSessionManager.getUserSession(message.from.id)

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
     * @see UserSession
     */
    private fun handleActionWithSession(message: Message, userSession: UserSession): HandlerResponse {
        val messages = when (userSession) {
            is UpdateRates -> handleUpdateRatesWithSession(message, userSession)
            else -> listOf(buildAnswerMessage(message.chatId, commandNotSupportedErrorMessage))
        }
        return HandlerResponse.Basic(messages)
    }

    private fun handleUpdateRatesWithoutSession(message: Message): HandlerResponse.Basic {
        val houses = adminService.getHouses(message.from.id)
        userSessionManager.startSession(message.from.id, UpdateRates.SelectHouse(houses))

        return HandlerResponse.Basic(
                listOf(buildAnswerMessage(message.chatId, selectHouseMessage, createHousesKeyboard(houses)))
        )
    }

    /**
     * Обработка сообщений на изменение тарифов в зависимости от актуально сессии [UpdateRates].
     *
     * @param message — Входное сообщение
     * @return Ответные сообщения по результатам обработки
     *
     * @see UpdateRates
     */
    private fun handleUpdateRatesWithSession(message: Message, userSession: UpdateRates): List<SendMessage> =
        when (userSession) {
            is UpdateRates.SelectHouse -> {
                val selectedHouse = userSession.houses.first { it.address == message.text }
                val services = adminService.getPublicServices(selectedHouse.id)
                    .sortedBy { it.id }

                userSessionManager.startSession(message.from.id, UpdateRates.Update(services))

                createRatesUpdateMessages(message.chatId, services)
            }

            is UpdateRates.Update -> {
                parseRates(message.text, userSession.publicServices).forEach { (id, value) ->
                    adminService.setRate(id, value)
                }

                userSessionManager.resetUserSession(message.from.id)

                listOf(buildAnswerMessage(message.chatId, dataSavedMessage))
            }
        }

    private fun handleTriggerCalculationWithoutSession(message: Message) : HandlerResponse.Broadcast {
        val messagesToAdmin: List<SendMessage> = listOf(
            buildAnswerMessage(message.chatId, billsSentMessage)
        )
        // TODO: provide real data from service
        //  1. find chat id of client by telegram id
        //  2. build document and pair to chat id
        val broadcastToUsers: List<BroadcastData> = listOf()

        return HandlerResponse.Broadcast(messagesToAdmin, broadcastToUsers)
    }
}
