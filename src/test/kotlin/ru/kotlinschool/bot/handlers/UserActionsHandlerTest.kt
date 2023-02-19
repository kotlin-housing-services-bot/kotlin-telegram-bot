package ru.kotlinschool.bot.handlers

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.telegram.telegrambots.meta.api.objects.Message
import ru.kotlinschool.bot.SessionManager
import ru.kotlinschool.bot.handlers.model.HandlerResponse
import ru.kotlinschool.bot.handlers.model.AddMetricsRequest
import ru.kotlinschool.bot.handlers.model.SessionAwareRequest
import ru.kotlinschool.bot.ui.Command
import ru.kotlinschool.bot.ui.createSelectFlatKeyboard
import ru.kotlinschool.bot.ui.selectFlatMessage
import ru.kotlinschool.data.FlatData
import ru.kotlinschool.data.HouseData
import ru.kotlinschool.service.UserService
import ru.kotlinschool.util.buildAnswerMessage
import ru.kotlinschool.util.createHousesMessages

private const val TEST_ID = 1L
private val TEST_HOUSE = HouseData(1, "адрес")
private val TEST_FLAT = FlatData(1, "адрес")


class UserActionsHandlerTest {

    private val sessionManager: SessionManager = mockk(relaxed = true) {
    }

    private val userService: UserService = mockk {
        every { getHouses(any()) } returns listOf(TEST_HOUSE)
        every { getFlats(any()) } returns listOf(TEST_FLAT)
    }

    private val userActionsHandler = UserActionsHandler(sessionManager, userService)

    @AfterEach
    fun prepare() {
        clearAllMocks()
    }

    @Test
    fun `handle flat registration start`() {
        // prepare specific mocks
        every { sessionManager.getUserSession(any()) } returns null

        val message: Message = mockMessage(Command.User.RegisterFlat.commandText)

        // prepare expected data
        val expected = createHousesMessages(TEST_ID, userService.getHouses(TEST_ID))

        userActionsHandler.handle(message) { response ->
            // check
            assertInstanceOf(HandlerResponse.Basic::class.java, response)
            assertEquals(expected, (response as HandlerResponse.Basic).messages)
            verify { sessionManager.startSession(TEST_ID, SessionAwareRequest.FlatRegistrationRequest) }
        }
    }

    @Test
    fun `handle add meter readings start`() {
        // prepare specific mocks
        every { sessionManager.getUserSession(any()) } returns null
        val message: Message = mockMessage(Command.User.AddMeterReadings.commandText)

        val expectedFlats = userService.getFlats(TEST_ID)
        val expectedKeyboard = createSelectFlatKeyboard(expectedFlats)
        val expected = listOf(buildAnswerMessage(message.chatId, selectFlatMessage, expectedKeyboard))

        userActionsHandler.handle(message) { response ->
            // check
            assertInstanceOf(HandlerResponse.Basic::class.java, response)
            assertEquals(expected, (response as HandlerResponse.Basic).messages)
            verify { sessionManager.startSession(TEST_ID, AddMetricsRequest.SelectFlatRequest(expectedFlats)) }
        }
    }


    private fun mockMessage(mockText: String): Message =
        mockk(relaxed = true) {
            every { from } returns mockk(relaxed = true) {
                every { id } returns TEST_ID
            }
            every { text } returns mockText
            every { chatId } returns TEST_ID
        }
}