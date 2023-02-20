package ru.kotlinschool.exception

class ValidationException(message: String? = null) : IllegalArgumentException(message)


fun <T> validate(value: T?, errMsg: String, predicate: (T) -> Boolean): T {
    return value.also { predicate } ?: throw ValidationException(errMsg)
}