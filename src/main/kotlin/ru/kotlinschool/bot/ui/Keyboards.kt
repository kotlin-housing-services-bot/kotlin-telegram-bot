package ru.kotlinschool.bot.ui

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import ru.kotlinschool.data.FlatData
import ru.kotlinschool.data.HouseData
import java.time.Year


val CANCEL_KEYBOARD: ReplyKeyboard = ReplyKeyboardMarkup().apply {
    keyboard = listOf(KeyboardRow(listOf(KeyboardButton(Command.Cancel.commandText))))
    resizeKeyboard = true
}

val CLEARED_KEYBOARD: ReplyKeyboard = ReplyKeyboardRemove().apply { removeKeyboard = true }

val REQUEST_BILL_YEAR_KEYBOARD: ReplyKeyboard = ReplyKeyboardMarkup().apply {
    keyboard = listOf(
        KeyboardRow(listOf(KeyboardButton(Year.now().value.toString()))),
        KeyboardRow(listOf(KeyboardButton(Command.Cancel.commandText))),
    )
    resizeKeyboard = true
}

val REQUEST_BILL_MONTH_KEYBOARD: ReplyKeyboard = ReplyKeyboardMarkup().apply {
    keyboard = listOf(
        KeyboardRow(listOf(KeyboardButton(Command.Cancel.commandText))),
        KeyboardRow(
            listOf(KeyboardButton("Январь - 1"), KeyboardButton("Февраль - 2"), KeyboardButton("Март - 3"))
        ),
        KeyboardRow(
            listOf(KeyboardButton("Апрель - 4"), KeyboardButton("Май - 5"), KeyboardButton("Июнь - 6"))
        ),
        KeyboardRow(
            listOf(KeyboardButton("Июль - 7"), KeyboardButton("Август - 8"), KeyboardButton("Сентябрь - 9"))
        ),
        KeyboardRow(
            listOf(KeyboardButton("Октябрь - 10"), KeyboardButton("Ноябрь - 11"), KeyboardButton("Декабрь - 12"))
        ),
    )
    resizeKeyboard = true
}


val START_KEYBOARD_USER: ReplyKeyboard = ReplyKeyboardMarkup().apply {
    keyboard = listOf(
        KeyboardRow(listOf(KeyboardButton(Command.User.RegisterFlat.commandText))),
        KeyboardRow(listOf(KeyboardButton(Command.User.AddMeterReadings.commandText))),
        KeyboardRow(listOf(KeyboardButton(Command.User.RequestOldBill.commandText))),
        KeyboardRow(listOf(KeyboardButton(Command.User.RequestDraftBill.commandText))),
    )
    resizeKeyboard = true
}

val START_KEYBOARD_ADMIN: ReplyKeyboard = ReplyKeyboardMarkup().apply {
    keyboard = listOf(
        KeyboardRow(listOf(KeyboardButton(Command.Admin.TriggerNotify.commandText))),
        KeyboardRow(listOf(KeyboardButton(Command.Admin.UpdateRates.commandText))),
        KeyboardRow(listOf(KeyboardButton(Command.Admin.TriggerCalculations.commandText))),
    )
    resizeKeyboard = true
}

val NO_FLAT_USER: ReplyKeyboard = ReplyKeyboardMarkup().apply {
    keyboard = listOf(
        KeyboardRow(listOf(KeyboardButton(Command.User.RegisterFlat.commandText))),
        KeyboardRow(listOf(KeyboardButton(Command.Cancel.commandText)))
    )
    resizeKeyboard = true
}

fun createSelectFlatKeyboard(flats: List<FlatData>): ReplyKeyboard = ReplyKeyboardMarkup().apply {
    keyboard = listOf(KeyboardRow(listOf(KeyboardButton(Command.Cancel.commandText)))) + flats.map {
        KeyboardRow(listOf(KeyboardButton(it.address)))
    }
    resizeKeyboard = true
}

fun createHousesKeyboard(houses: List<HouseData>): ReplyKeyboard = ReplyKeyboardMarkup().apply {
    keyboard = listOf(KeyboardRow(listOf(KeyboardButton(Command.Cancel.commandText)))) + houses.map {
        KeyboardRow(listOf(KeyboardButton(it.address)))
    }
    resizeKeyboard = true
}
