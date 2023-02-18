package ru.kotlinschool.exception

import ru.kotlinschool.bot.ui.flatIsNotRegisteredMessage

class FlatNotRegisteredException: IllegalStateException(flatIsNotRegisteredMessage)
