package ru.kotlinschool.exception

import ru.kotlinschool.bot.ui.tooManyMetricAdditionsError

class TooManyMetricAdditionsException: IllegalArgumentException(tooManyMetricAdditionsError)
