package ru.kotlinschool.exception

class ValidationException(message: String? = null) : IllegalArgumentException(message)


fun <T> validate(value: T?, errMsg: String, predicate: (T) -> Boolean = { true }): T {
    if (value == null || predicate.invoke(value)) throw ValidationException(errMsg)
    else return value
}