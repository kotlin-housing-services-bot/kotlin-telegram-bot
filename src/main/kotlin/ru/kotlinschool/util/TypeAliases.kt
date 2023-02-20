package ru.kotlinschool.util

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod
import ru.kotlinschool.bot.handlers.model.HandlerResponse
import java.io.Serializable

typealias BotApiMethod = PartialBotApiMethod<out Serializable>

typealias ResponseCallback = (HandlerResponse) -> Unit
