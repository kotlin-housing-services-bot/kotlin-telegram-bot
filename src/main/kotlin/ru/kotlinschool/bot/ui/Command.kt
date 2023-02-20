package ru.kotlinschool.bot.ui

/**
 * Команды для взаимодействия с пользователем.
 *
 * @param commandText — Текст сообщения для вызова команды
 */
sealed class Command(val commandText: String) {

    object Start : Command("/start")
    object Stop : Command("/stop")
    object Cancel : Command("🛑 Отмена")

    sealed class Admin(message: String) : Command(message) {

        object TriggerNotify : Admin("🗓 Напомнить о необходимости внесения данных")
        object TriggerCalculations : Admin("🧮 Провести вычисления")
        object UpdateRates : Admin("🎚 Обновить тарифы")
    }

    sealed class User(message: String) : Command(message) {

        object RegisterFlat : User("📝 Регистрация квартиры")
        object AddMeterReadings : User("🎛 Ввести данные счётчиков")
        object RequestOldBill : User("🗓 Посмотреть старую платёжку")
        object RequestDraftBill : User("🫥 Запросить черновик платёжки")
    }
}
