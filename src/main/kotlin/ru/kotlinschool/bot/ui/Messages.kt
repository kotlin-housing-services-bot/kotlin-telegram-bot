package ru.kotlinschool.bot.ui

import ru.kotlinschool.dto.PublicServiceDto

// errors
const val commandNotSupportedErrorMessage = "Команда не поддерживается"
const val flatAreaErrorMessage = "Площадь квартиры введена неверно"
const val flatNumErrorMessage = "Номер квартиры введён неверно"
const val formatErrorMessage = "Нарушен формат ввода"
const val houseIdErrorMessage = "Идентификатор дома введён неверно"
const val preserveDataOrderMessage = "‼️Обязательно сохраните порядок данных в списке, чтоб избежать ошибок в расчёте‼️"
const val residentsErrorMessage = "Количество жильцов введено неверно"
const val unknownError = "Неизвестная ошибка"

// common
const val farewellMessage = "До встречи!"
const val dataSavedMessage = "Сохранили!"
const val retryMessage = "Давайте попробуем ещё раз"
const val welcomeMessage = "Добрый день!"

// user
// flat
const val addFlatRecommendationMessage = "Хотите прикрепить?"
const val flatIsNotRegisteredMessage = "Похоже вы не прикрепили квартиру"
val flatRegistrationMessage = """
1. Идентификатор дома из списка
2. Номер квартиры
3. Площадь квартиры в м^2
4. Количество жителей
""".trimIndent()
const val selectFlatMessage = "Выберите квартиру"

// bill
const val selectMonthMessage = "За какой месяц?"

// meter readings
const val addMeterReadingsMessage = "Необходимо добавить текущие показания"
const val enterMeterReadingsHeaderMessage = "Введите значения счётчиков"
const val enterMeterReadingsFormatMessage = "Формат значений:\n000.000"
const val flatRegistrationMessageHeaderMessage = "Введите данные квартиры"

// house
const val housesMessageTemplate = "Доступные дома:\nИдентификатор - Адрес"

// admin
//calculations
const val billsSentMessage = "Счета разосланы"

// house
const val selectHouseMessage = "Выберите дом"

// rates
const val ratesUpdateHeaderMessage = "Введите новые тарифы"
const val ratesUpdateFormatMessage = "Формат значений: 00000000"

