package ru.kotlinschool.exception

import ru.kotlinschool.bot.ui.yearNotSupportedError

class YearNotSupportedException: IllegalArgumentException(yearNotSupportedError)
