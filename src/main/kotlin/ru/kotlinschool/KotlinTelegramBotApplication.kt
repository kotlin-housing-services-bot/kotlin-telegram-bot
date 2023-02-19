package ru.kotlinschool

import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import ru.kotlinschool.bot.HousingBot

@SpringBootApplication
class KotlinTelegramBotApplication

fun main(args: Array<String>) {
    runApplication<KotlinTelegramBotApplication>(*args).run {
        TelegramBotsApi(DefaultBotSession::class.java).run {
            getBean<HousingBot>().let(::registerBot)
        }
    }
}
