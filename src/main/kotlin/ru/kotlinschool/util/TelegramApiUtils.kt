package ru.kotlinschool.util

import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import ru.kotlinschool.bot.ui.CANCEL_KEYBOARD
import ru.kotlinschool.bot.ui.CLEARED_KEYBOARD
import ru.kotlinschool.bot.ui.enterMeterReadingsFormatMessage
import ru.kotlinschool.bot.ui.enterMeterReadingsHeaderMessage
import ru.kotlinschool.bot.ui.flatRegistrationMessage
import ru.kotlinschool.bot.ui.flatRegistrationMessageHeaderMessage
import ru.kotlinschool.bot.ui.preserveDataOrderMessage
import ru.kotlinschool.bot.ui.ratesUpdateFormatMessage
import ru.kotlinschool.bot.ui.ratesUpdateHeaderMessage
import ru.kotlinschool.data.PublicServiceData

private val LINE_SEPARATOR = System.lineSeparator()

fun createFlatRegistrationMessages(chatId: Long) =
    listOf(
        buildAnswerMessage(chatId, flatRegistrationMessageHeaderMessage),
        buildAnswerMessage(chatId, preserveDataOrderMessage),
        buildAnswerMessage(chatId, flatRegistrationMessage, CANCEL_KEYBOARD)
    )

fun createPublicServicesMessages(chatId: Long, publicServices: List<PublicServiceData>) =
    mutableListOf(
        buildAnswerMessage(chatId, enterMeterReadingsHeaderMessage),
        buildAnswerMessage(chatId, preserveDataOrderMessage),
        buildAnswerMessage(chatId, enterMeterReadingsFormatMessage),
    ).apply {
        val publicServicesMessage = createPublicServiceListMessage(publicServices)
        add(buildAnswerMessage(chatId, publicServicesMessage, CANCEL_KEYBOARD))
    }

fun createRatesUpdateMessages(chatId: Long, publicServices: List<PublicServiceData>) =
    mutableListOf(
        buildAnswerMessage(chatId, ratesUpdateHeaderMessage),
        buildAnswerMessage(chatId, preserveDataOrderMessage),
        buildAnswerMessage(chatId, ratesUpdateFormatMessage),
    ).apply {
        val publicServicesMessage = createPublicServiceListMessage(publicServices)
        add(buildAnswerMessage(chatId, publicServicesMessage, CANCEL_KEYBOARD))
    }

fun createPublicServiceListMessage(publicServices: List<PublicServiceData>) =
    publicServices.withIndex().joinToString(LINE_SEPARATOR) { "${it.index + 1}. ${it.value.name}" }

fun buildAnswerMessage(
    targetChatId: Long,
    messageText: String,
    keyboard: ReplyKeyboard = CLEARED_KEYBOARD
) = SendMessage().apply {
    chatId = targetChatId.toString()
    text = messageText
    replyMarkup = keyboard
}

fun buildAnswerDocument(
    targetChatId: Long,
    fileData: InputFile,
    keyboard: ReplyKeyboard = CLEARED_KEYBOARD
) = SendDocument().apply {
    chatId = targetChatId.toString()
    document = fileData
    replyMarkup = keyboard
}
