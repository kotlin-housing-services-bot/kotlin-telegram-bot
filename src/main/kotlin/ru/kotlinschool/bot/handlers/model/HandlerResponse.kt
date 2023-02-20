package ru.kotlinschool.bot.handlers.model

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import ru.kotlinschool.util.BotApiMethod


sealed class HandlerResponse {

    class Basic(val messages: List<BotApiMethod>) : HandlerResponse()

    class Broadcast(
        val messagesToAdmin: List<SendMessage>,
        val broadcastMessagesToUsers: List<BotApiMethod>
    ) : HandlerResponse()
}

