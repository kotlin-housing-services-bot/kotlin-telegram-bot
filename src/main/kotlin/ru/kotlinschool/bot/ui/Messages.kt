package ru.kotlinschool.bot.ui

// ~~ ERRORS
const val commandNotSupportedErrorMessage = "Команда не поддерживается🧐"
const val flatAreaErrorMessage = "Площадь квартиры введена неверно🧐"
const val flatNumErrorMessage = "Номер квартиры введён неверно🧐"
const val formatErrorMessage = "Нарушен формат ввода🧐"
const val houseNotRegisteredErrorMessage = "Этот дом не подключен🧐"
const val preserveDataOrderMessage = "‼️Обязательно сохраните порядок данных в списке, чтоб избежать ошибок в расчёте‼️"
const val residentsErrorMessage = "Количество жильцов введено неверно🧐"
const val tooManyMetricAdditionsError = "Вы уже вводили сегодня показания🧐"
const val unknownError = "Неизвестная ошибка😱"
const val yearError = "Ошибка при вводе года🧐"
const val yearNotSupportedError = "Получить платёжку по прошедшим и будущим годам пока нельзя, извините😢"

// ~~ COMMON
const val anotherTimeMessage = "Давайте попробуем позже"
const val commandUnderDevelopmentMessage = "Команда в разработке, извините за неудобства🫥"
const val farewellMessage = "До встречи!🙃"
const val dataSavedMessage = "Сохранили!✅"
const val retryMessage = "Давайте попробуем ещё раз"
const val welcomeMessage = "Добрый день!👋"

// ~~ USER
// flat
const val addFlatRecommendationMessage = "Хотите прикрепить?"
const val flatIsNotRegisteredMessage = "Похоже вы не прикрепили квартиру"
val flatRegistrationMessage = """
1. Номер квартиры
2. Площадь квартиры в м^2
3. Количество жителей
""".trimIndent()

const val selectFlatMessage = "Выберите квартиру👀"

// bill
const val billFound = "Ваш счет на оплату: "
const val billNotFound = "Не найдено квитанции по заданным параметрам🤔"
const val newPaymentBill = "🗓Пришли новые счета на оплату!🗓"
const val selectMonthMessage = "За какой месяц?"
const val selectYearMessage = "За какой год?"

// metrics
const val addMeterReadingNotification = "‼️Необходимо передать показания за текущий месяц‼️"
const val addMeterReadingsMessage = "Необходимо добавить текущие показания📝"
const val enterMeterReadingsHeaderMessage = "Введите значения счётчиков📝"
val enterMeterReadingsFormatMessage = "Формат значений: XXX.XXX".trimIndent()

const val flatRegistrationMessageHeaderMessage = "Введите данные квартиры📝"

// ~~ ADMIN
//calculations
const val billsSentMessage = "Счета разосланы✅"
// notifications
const val notificationsSentMessage = "Уведомления разосланы✅"
// house
const val selectHouseMessage = "Выберите дом🏘"

// rates
const val ratesUpdateHeaderMessage = "Введите новые тарифы📝"
const val ratesUpdateFormatMessage = "Формат значений: XXXXXXXX"

