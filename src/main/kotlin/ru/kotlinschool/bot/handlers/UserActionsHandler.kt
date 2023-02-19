package ru.kotlinschool.bot.handlers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import ru.kotlinschool.bot.UserSessionManager
import ru.kotlinschool.bot.handlers.entities.HandlerResponse
import ru.kotlinschool.bot.handlers.entities.MeterReadingsAdd
import ru.kotlinschool.bot.handlers.entities.PreviousBill
import ru.kotlinschool.bot.handlers.entities.UserSession
import ru.kotlinschool.bot.ui.CANCEL_KEYBOARD
import ru.kotlinschool.bot.ui.Command
import ru.kotlinschool.bot.ui.NO_FLAT_USER
import ru.kotlinschool.bot.ui.REQUEST_BILL_KEYBOARD
import ru.kotlinschool.bot.ui.addFlatRecommendationMessage
import ru.kotlinschool.bot.ui.addMeterReadingsMessage
import ru.kotlinschool.bot.ui.commandNotSupportedErrorMessage
import ru.kotlinschool.bot.ui.createSelectFlatKeyboard
import ru.kotlinschool.bot.ui.dataSavedMessage
import ru.kotlinschool.bot.ui.retryMessage
import ru.kotlinschool.bot.ui.selectFlatMessage
import ru.kotlinschool.bot.ui.selectMonthMessage
import ru.kotlinschool.bot.ui.unknownError
import ru.kotlinschool.data.HouseData
import ru.kotlinschool.exception.EntityNotFoundException
import ru.kotlinschool.exception.FlatNotRegisteredException
import ru.kotlinschool.exception.ParserException
import ru.kotlinschool.persistent.entity.CalculationType
import ru.kotlinschool.service.UserService

/**
 * Обработчик команд от обычного пользователя.
 *
 * @see UserSessionManager
 */
@Component
@Suppress("TODO")
class UserActionsHandler @Autowired constructor(
    private val userSessionManager: UserSessionManager,
    private val userService: UserService,
) {

    /**
     * Основная точка входа. Разводит обработку команд на зависимые от текущей сессии пользователя и обычные.
     *
     * @see handleTextAction
     * @see handleActionWithSession
     *
     * @see ResponseCallback
     * @see HandlerResponse.Basic
     */
    fun handle(message: Message, callback: ResponseCallback) {
        val answerMessages = runCatching {
            val userSession = userSessionManager.getUserSession(message.from.id)

            if (userSession == null) {
                handleTextAction(message)
            } else {
                handleActionWithSession(message, userSession)
            }
        }.getOrElse { error ->
            // TODO: log
            when (error) {
                is EntityNotFoundException -> listOf(
                    buildAnswerMessage(message.chatId, error.message),
                    buildAnswerMessage(message.chatId, retryMessage, CANCEL_KEYBOARD)
                )
                is ParserException -> listOf(
                    buildAnswerMessage(message.chatId, error.message.orEmpty()),
                    buildAnswerMessage(message.chatId, retryMessage, CANCEL_KEYBOARD)
                )
                is FlatNotRegisteredException -> listOf(
                    buildAnswerMessage(message.chatId, error.message.orEmpty()),
                    buildAnswerMessage(message.chatId, addFlatRecommendationMessage, NO_FLAT_USER)
                )
                else -> listOf(
                    buildAnswerMessage(message.chatId, unknownError),
                    buildAnswerMessage(message.chatId, retryMessage, CANCEL_KEYBOARD)
                )
            }
        }

        callback(HandlerResponse.Basic(answerMessages))
    }

    /**
     *
     * Обработчик команд, которые не зависят от текущей сессии.
     * Преимущественно стартует новую сессию для пользователя.
     *
     * @param message — Входное сообщение
     * @return Ответные сообщения по результатам обработки
     *
     * @see handleActionWithSession
     *
     * @see SendMessage
     */
    private fun handleTextAction(message: Message): List<SendMessage> =
        when (message.text) {
            Command.User.RegisterFlat.commandText -> {
                userSessionManager.startSession(message.from.id, UserSession.FlatRegistration)

                // TODO: use real management company
                val houses = userService.getHouses(1).sortedBy(HouseData::id)
                createHousesMessages(message.chatId, houses)
            }

            Command.User.RequestOldBill.commandText -> {
                userSessionManager.startSession(message.from.id, PreviousBill.StartRequest)
                listOf(buildAnswerMessage(message.chatId, selectMonthMessage, REQUEST_BILL_KEYBOARD))
            }

            Command.User.AddMeterReadings.commandText -> startAddingMeterReadings(message)

            Command.User.RequestDraftBill.commandText -> TODO()

            else -> listOf(buildAnswerMessage(message.chatId, commandNotSupportedErrorMessage))
        }

    /**
     * Обработчик команд пользователя в рамках некоторой сессии.
     *
     * @param message — Входное сообщение
     * @return Ответные сообщения по результатам обработки
     *
     * @see UserSession
     */
    private fun handleActionWithSession(message: Message, userSession: UserSession): List<SendMessage> =
        when (userSession) {
            is UserSession.FlatRegistration -> handleFlatRegistration(message)
            is MeterReadingsAdd -> handleMeterReadingsUpdate(message, userSession)
            is PreviousBill -> handlePreviousBillRequest(message, userSession)
            else -> listOf(buildAnswerMessage(message.chatId, commandNotSupportedErrorMessage))
        }

    /**
     * Продолжение обработки регистрации квартиры в рамках сессии [UserSession.FlatRegistration].
     * Переводит пользователя на ввод значений счётчика для квартиры и бновляет состояние текущей сессии
     * пользователя на [MeterReadingsAdd.SelectFlat].
     *
     * @param message — Входное сообщение
     * @return Ответные сообщения с уведомлением о необходимости выбрать квартиру для ввода данных
     *
     * @see handleTextAction
     * @see startAddingMeterReadings
     *
     */
    @Throws(ParserException::class)
    private fun handleFlatRegistration(message: Message): List<SendMessage> {
        val (houseId, flatNum, area, residentsNum) = parseFlatData(message.text)

        userService.registerFlat(message.from.id, message.chatId, houseId, flatNum, area, residentsNum)

        return startAddingMeterReadings(message, isInitial = true)
    }

    /**
     * Запуск процедуры добавления значений счётчиков
     *
     * @param message — Входное сообщение
     * @param isInitial — Вызвов произошёл в рамках добавления квартиры
     * @return Ответные сообщения с приглашением выбрать квартиру для ввода данных
     *
     * @throws FlatNotRegisteredException в случае, когда у пользователя нет зарегестрированных квартир
     */
    @Throws(FlatNotRegisteredException::class)
    private fun startAddingMeterReadings(message: Message, isInitial: Boolean = false): List<SendMessage> {
        val flats = userService.getFlats(message.from.id).takeIf { it.isNotEmpty() }
            ?: throw FlatNotRegisteredException()

        userSessionManager.startSession(message.from.id, MeterReadingsAdd.SelectFlat(flats))

        return mutableListOf<SendMessage>().apply {
            if (isInitial) {
                add(buildAnswerMessage(message.chatId, addMeterReadingsMessage))
            }
            add(buildAnswerMessage(message.chatId, selectFlatMessage, createSelectFlatKeyboard(flats)))
        }
    }

    /**
     * Обработка добавления данных счётчика в соответствии с текущей сессией пользователя.
     * При  текущей сессии [MeterReadingsAdd.SelectFlat] пригласит ввести значения счётчика и переведёт
     * в [MeterReadingsAdd.Add]. При [MeterReadingsAdd.Add] сохраняет данные и сбрасывает текущую сессию.
     *
     * @param message — Входное сообщение
     * @param userSession — Текущая пользователься сессия
     * @return Ответные сообщения с уведомлением о необходимости ввести данные или с уведомлением о результате
     *
     * @see MeterReadingsAdd
     *
     * @throws FlatNotRegisteredException
     */
    @Throws(FlatNotRegisteredException::class)
    private fun handleMeterReadingsUpdate(message: Message, userSession: MeterReadingsAdd): List<SendMessage> =
        when (userSession) {
            is MeterReadingsAdd.SelectFlat -> {
                val flat = userSession.flats.firstOrNull { it.address == message.text }
                    ?: throw FlatNotRegisteredException()

                val publicServices = userService.getPublicServices(flat.id)
                    .filter { it.calculationType == CalculationType.BY_METER }
                    .sortedBy { it.id }

                userSessionManager.startSession(message.from.id, MeterReadingsAdd.Add(flat, publicServices))

                createPublicServicesMessages(message.chatId, publicServices)
            }

            is MeterReadingsAdd.Add -> {
                parseMeterReadings(message.text, userSession.publicServices).forEach { (serviceId, value) ->
                    userService.addMetric(userSession.flat.id, serviceId, value)
                }
                userSessionManager.resetUserSession(message.from.id)

                listOf(buildAnswerMessage(message.chatId, dataSavedMessage))
            }
        }

    /**
     * Обработка получения старой платёжки в соответствии с текущей сессией пользователя
     *
     * @param message — Входное сообщение
     * @param userSession — Текущая пользователься сессия
     * @return Ответные сообщения с уведомлением о необходимости ввести данные или с уведомлением о результате
     *
     * @see PreviousBill
     *
     * @throws FlatNotRegisteredException
     */
    @Throws(FlatNotRegisteredException::class)
    private fun handlePreviousBillRequest(message: Message, userSession: PreviousBill): List<SendMessage> =
        when (userSession) {
            is PreviousBill.StartRequest -> {
                val flats = userService.getFlats(message.from.id).takeIf { it.isNotEmpty() }
                    ?: throw FlatNotRegisteredException()

                userSessionManager.startSession(message.from.id, PreviousBill.SelectFlat(flats))

                val keyboard = createSelectFlatKeyboard(flats)
                listOf(buildAnswerMessage(message.chatId, selectFlatMessage, keyboard))
            }
            is PreviousBill.SelectFlat -> {
                val flats = userSession.flats.first { it.address == message.text }
                userSessionManager.startSession(message.from.id, PreviousBill.SelectMonth(flats))

                listOf(buildAnswerMessage(message.chatId, selectMonthMessage, REQUEST_BILL_KEYBOARD))
            }
            is PreviousBill.SelectMonth -> {
//                TODO: parse month and get bill
//                userService.getBill()
                TODO()
            }
        }
}
