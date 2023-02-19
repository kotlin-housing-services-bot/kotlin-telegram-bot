package ru.kotlinschool.bot.handlers.entities

import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

data class BroadcastData(val message: SendMessage, val document: SendDocument? = null)

sealed class HandlerResponse {

    class Basic(val messages: List<SendMessage>) : HandlerResponse()

    class Broadcast(
        val messages: List<SendMessage>,
        val broadcastMessages: List<BroadcastData>
    ) : HandlerResponse()
}

