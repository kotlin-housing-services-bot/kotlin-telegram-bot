package ru.kotlinschool.bot.handlers.model

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.io.Serializable

sealed class HandlerResponse {

    class Basic(val messages: List<PartialBotApiMethod<out Serializable>>) : HandlerResponse()

    class Broadcast(
        val messagesToAdmin: List<SendMessage>,
        val broadcastMessagesToUsers: List<PartialBotApiMethod<out Serializable>>
    ) : HandlerResponse()
}

