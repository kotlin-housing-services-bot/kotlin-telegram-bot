package ru.kotlinschool.bot.handlers

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import ru.kotlinschool.bot.handlers.model.AddMetricsRequest
import ru.kotlinschool.bot.handlers.model.FlatRegistrationRequest
import ru.kotlinschool.bot.handlers.model.HandlerResponse
import ru.kotlinschool.bot.handlers.model.PreviousBillRequest
import ru.kotlinschool.bot.handlers.model.SessionAwareRequest
import ru.kotlinschool.bot.session.SessionManager
import ru.kotlinschool.bot.ui.CANCEL_KEYBOARD
import ru.kotlinschool.bot.ui.Command
import ru.kotlinschool.bot.ui.NO_FLAT_USER
import ru.kotlinschool.bot.ui.REQUEST_BILL_MONTH_KEYBOARD
import ru.kotlinschool.bot.ui.REQUEST_BILL_YEAR_KEYBOARD
import ru.kotlinschool.bot.ui.START_KEYBOARD_USER
import ru.kotlinschool.bot.ui.addFlatRecommendationMessage
import ru.kotlinschool.bot.ui.addMeterReadingsMessage
import ru.kotlinschool.bot.ui.anotherTimeMessage
import ru.kotlinschool.bot.ui.billFound
import ru.kotlinschool.bot.ui.billNotFound
import ru.kotlinschool.bot.ui.commandNotSupportedErrorMessage
import ru.kotlinschool.bot.ui.commandUnderDevelopmentMessage
import ru.kotlinschool.bot.ui.createHousesKeyboard
import ru.kotlinschool.bot.ui.createSelectFlatKeyboard
import ru.kotlinschool.bot.ui.dataSavedMessage
import ru.kotlinschool.bot.ui.retryMessage
import ru.kotlinschool.bot.ui.selectFlatMessage
import ru.kotlinschool.bot.ui.selectHouseMessage
import ru.kotlinschool.bot.ui.selectMonthMessage
import ru.kotlinschool.bot.ui.selectYearMessage
import ru.kotlinschool.bot.ui.unknownError
import ru.kotlinschool.data.FlatData
import ru.kotlinschool.data.HouseData
import ru.kotlinschool.data.PublicServiceData
import ru.kotlinschool.exception.EntityNotFoundException
import ru.kotlinschool.exception.FlatNotRegisteredException
import ru.kotlinschool.exception.HouseNotRegisteredException
import ru.kotlinschool.exception.ParserException
import ru.kotlinschool.exception.TooManyMetricAdditionsException
import ru.kotlinschool.exception.ValidationException
import ru.kotlinschool.exception.YearNotSupportedException
import ru.kotlinschool.persistent.entity.CalculationType
import ru.kotlinschool.service.UserService
import ru.kotlinschool.util.BotApiMethod
import ru.kotlinschool.util.ResponseCallback
import ru.kotlinschool.util.buildAnswerDocument
import ru.kotlinschool.util.buildAnswerMessage
import ru.kotlinschool.util.createFlatRegistrationMessages
import ru.kotlinschool.util.createPublicServicesMessages
import ru.kotlinschool.util.generateBillName
import ru.kotlinschool.util.parseFlatData
import ru.kotlinschool.util.parseMeterReadings
import ru.kotlinschool.util.parseMonthMessageGetNumber
import ru.kotlinschool.util.parseYear
import java.io.ByteArrayInputStream

/**
 * Обработчик команд от обычного пользователя.
 *
 * @see SessionManager
 */
@Component
@Suppress("TODO")
class UserActionsHandler(
    private val sessionManager: SessionManager,
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
            val userSession = sessionManager.getUserSession(message.from.id)

            if (userSession == null) {
                handleTextAction(message)
            } else {
                handleActionWithSession(message, userSession)
            }
        }.getOrElse { error ->
            error.printStackTrace()
            val chatId = message.chatId
            when (error) {
                is EntityNotFoundException -> listOf(
                    buildAnswerMessage(chatId, error.message),
                    buildAnswerMessage(chatId, retryMessage, CANCEL_KEYBOARD)
                )
                is ParserException -> listOf(
                    buildAnswerMessage(chatId, error.message.orEmpty()),
                    buildAnswerMessage(chatId, retryMessage, CANCEL_KEYBOARD)
                )
                is FlatNotRegisteredException -> {
                    sessionManager.resetUserSession(message.from.id)
                    listOf(
                        buildAnswerMessage(chatId, error.message.orEmpty()),
                        buildAnswerMessage(chatId, addFlatRecommendationMessage, NO_FLAT_USER)
                    )
                }
                is HouseNotRegisteredException,
                is YearNotSupportedException -> {
                    sessionManager.resetUserSession(message.from.id)
                    listOf(
                        buildAnswerMessage(chatId, error.message.orEmpty()),
                        buildAnswerMessage(chatId, retryMessage, START_KEYBOARD_USER)
                    )
                }
                is TooManyMetricAdditionsException -> {
                    sessionManager.resetUserSession(message.from.id)
                    listOf(
                        buildAnswerMessage(chatId, error.message.orEmpty()),
                        buildAnswerMessage(chatId, anotherTimeMessage, START_KEYBOARD_USER)
                    )
                }
                is ValidationException -> {
                    listOf(
                        buildAnswerMessage(chatId, error.message.orEmpty()),
                        buildAnswerMessage(chatId, retryMessage, START_KEYBOARD_USER)
                    )
                }
                else -> {
                    sessionManager.resetUserSession(message.from.id)
                    listOf(
                        buildAnswerMessage(chatId, unknownError),
                        buildAnswerMessage(chatId, anotherTimeMessage, START_KEYBOARD_USER)
                    )
                }
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
    private fun handleTextAction(message: Message): List<SendMessage> {
        val chatId = message.chatId
        val userId = message.from.id
        return when (message.text) {
            Command.User.RegisterFlat.commandText -> {
                // TODO: use real management company
                val houses = userService.getHouses(1).sortedBy(HouseData::id)
                sessionManager.startSession(userId, FlatRegistrationRequest.SelectHouseRequest(houses))

                listOf(buildAnswerMessage(chatId, selectHouseMessage, createHousesKeyboard(houses)))
            }

            Command.User.RequestOldBill.commandText -> {
                sessionManager.startSession(userId, PreviousBillRequest.SelectYearRequest)
                listOf(buildAnswerMessage(chatId, selectYearMessage, REQUEST_BILL_YEAR_KEYBOARD))
            }

            Command.User.AddMeterReadings.commandText -> startAddingMeterReadings(message)

            else -> listOf(buildAnswerMessage(chatId, commandUnderDevelopmentMessage))
        }
    }

    /**
     * Обработчик команд пользователя в рамках некоторой сессии.
     *
     * @param message — Входное сообщение
     * @return Ответные сообщения по результатам обработки
     *
     * @see SessionAwareRequest
     */
    private fun handleActionWithSession(
        message: Message,
        sessionAwareRequest: SessionAwareRequest
    ): List<BotApiMethod> =
        when (sessionAwareRequest) {
            is FlatRegistrationRequest -> handleFlatRegistration(message, sessionAwareRequest)
            is AddMetricsRequest -> handleMeterReadingsUpdate(message, sessionAwareRequest)
            is PreviousBillRequest -> handlePreviousBillRequest(message, sessionAwareRequest)
            else -> listOf(buildAnswerMessage(message.chatId, commandNotSupportedErrorMessage))
        }

    /**
     * Продолжение обработки регистрации квартиры в рамках сессии [SessionAwareRequest.FlatRegistrationRequest].
     * Переводит пользователя на ввод значений счётчика для квартиры и бновляет состояние текущей сессии
     * пользователя на [AddMetricsRequest.SelectFlatRequest].
     *
     * @param message — Входное сообщение
     * @return Ответные сообщения с уведомлением о необходимости выбрать квартиру для ввода данных
     *
     * @see handleTextAction
     * @see startAddingMeterReadings
     *
     * @throws HouseNotRegisteredException в случае, если пользователь ввеёл не прикреплённый дом
     */
    @Throws(HouseNotRegisteredException::class)
    private fun handleFlatRegistration(message: Message, request: FlatRegistrationRequest): List<SendMessage> {
        val id = message.from.id
        return when (request) {
            is FlatRegistrationRequest.SelectHouseRequest -> {
                val selectedHouse = request.houses.firstOrNull { it.address == message.text }
                    ?: throw HouseNotRegisteredException()

                sessionManager.startSession(id, FlatRegistrationRequest.FlatDataRequest(selectedHouse))

                createFlatRegistrationMessages(message.chatId)
            }

            is FlatRegistrationRequest.FlatDataRequest -> {
                val userId: Long = id
                val chatId: Long = message.chatId

                val houseId = request.house.id
                val (flatNum, area, residentsNum) = parseFlatData(message.text)

                val flatData = userService.registerFlat(userId, chatId, houseId, flatNum, area, residentsNum)

                startAddingMeterReadings(message, flatData, isInitial = true)
            }
        }
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
    private fun startAddingMeterReadings(message: Message, flatData: FlatData? = null, isInitial: Boolean = false): List<SendMessage> {
        val userId = message.from.id
        val flats = if (flatData != null) {
            listOf(flatData)
        } else {
            userService.getFlats(userId).takeIf { it.isNotEmpty() } ?: throw FlatNotRegisteredException()
        }

        sessionManager.startSession(userId, AddMetricsRequest.SelectFlatRequest(flats, isInitial))

        return mutableListOf<SendMessage>().apply {
            val chatId = message.chatId
            if (flatData != null) {
                add(buildAnswerMessage(chatId, addMeterReadingsMessage))
            }
            add(buildAnswerMessage(chatId, selectFlatMessage, createSelectFlatKeyboard(flats)))
        }
    }

    /**
     * Обработка добавления данных счётчика в соответствии с текущей сессией пользователя.
     * При  текущей сессии [AddMetricsRequest.SelectFlatRequest] пригласит ввести значения счётчика и переведёт
     * в [AddMetricsRequest.AddRequest]. При [AddMetricsRequest.AddRequest] сохраняет данные и сбрасывает текущую сессию.
     *
     * @param message — Входное сообщение
     * @param request — Текущая пользователься сессия
     * @return Ответные сообщения с уведомлением о необходимости ввести данные или с уведомлением о результате
     *
     * @see AddMetricsRequest
     *
     * @throws FlatNotRegisteredException
     */
    @Throws(FlatNotRegisteredException::class)
    private fun handleMeterReadingsUpdate(message: Message, request: AddMetricsRequest): List<SendMessage> {
        val chatId = message.chatId
        val userId = message.from.id
        val text = message.text
        return when (request) {
            is AddMetricsRequest.SelectFlatRequest -> {
                val flat = request.flats.firstOrNull { it.address == text }
                    ?: throw FlatNotRegisteredException()

                val publicServices = userService.getPublicServices(flat.id)
                    .filter { it.calculationType == CalculationType.BY_METER }
                    .sortedBy(PublicServiceData::id)

                val nextRequest = AddMetricsRequest.AddRequest(flat, publicServices, isInitial = request.isInitial)
                sessionManager.startSession(userId, nextRequest)

                createPublicServicesMessages(chatId, publicServices)
            }

            is AddMetricsRequest.AddRequest -> {
                parseMeterReadings(text, request.publicServices).forEach { (serviceId, value) ->
                    userService.addMetric(request.flat.id, serviceId, value, request.isInitial)
                }
                sessionManager.resetUserSession(userId)

                listOf(buildAnswerMessage(chatId, dataSavedMessage))
            }
        }
    }

    /**
     * Обработка получения старой платёжки в соответствии с текущей сессией пользователя
     *
     * @param message — Входное сообщение
     * @param request — Текущая пользователься сессия
     * @return Ответные сообщения с уведомлением о необходимости ввести данные или с уведомлением о результате
     *
     * @see PreviousBillRequest
     *
     * @throws FlatNotRegisteredException
     */
    @Throws(FlatNotRegisteredException::class, YearNotSupportedException::class)
    private fun handlePreviousBillRequest(message: Message, request: PreviousBillRequest): List<BotApiMethod> {
        val chatId = message.chatId
        val userId = message.from.id
        return when (request) {
            is PreviousBillRequest.SelectYearRequest -> {
                val year = parseYear(message.text)

                sessionManager.startSession(userId, PreviousBillRequest.SelectMonthRequest(year))

                listOf(buildAnswerMessage(chatId, selectMonthMessage, REQUEST_BILL_MONTH_KEYBOARD))
            }

            is PreviousBillRequest.SelectMonthRequest -> {
                val month = parseMonthMessageGetNumber(message.text)

                val flats = userService.getFlats(userId).takeIf { it.isNotEmpty() }
                    ?: throw FlatNotRegisteredException()

                val nextRequest = PreviousBillRequest.SelectFlatRequest(month, request.year, flats)
                sessionManager.startSession(userId, nextRequest)

                listOf(buildAnswerMessage(chatId, selectFlatMessage, createSelectFlatKeyboard(flats)))
            }

            is PreviousBillRequest.SelectFlatRequest -> {
                val flat = request.flats.firstOrNull { it.address == message.text }
                    ?: throw FlatNotRegisteredException()

                runCatching {
                    val data = userService.getBill(flat.id, request.year, request.month)
                    val name = generateBillName(flat.address, request.month, request.year)
                    val inputFile = InputFile(ByteArrayInputStream(data), name)

                    listOf(
                        buildAnswerMessage(chatId, billFound),
                        buildAnswerDocument(chatId, inputFile)
                    )
                }.getOrElse {
                    if (it is EntityNotFoundException) {
                        listOf(buildAnswerMessage(chatId, billNotFound))
                    } else {
                        throw it
                    }
                }
            }
        }
    }
}
