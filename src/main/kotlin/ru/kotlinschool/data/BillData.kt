package ru.kotlinschool.data

data class BillData(
    /**
     * Год
     */
    val year: Int,

    /**
     * Месяц
     */
    val month: Int,

    /**
     * Наименование УК
     */
    val managementCompanyName: String,

    /**
     * Адрес
     */
    val address: String,

    /**
     * Площадь
     */
    var area: Double,

    /**
     * Количество прописанных
     */
    var numberOfResidents: Long,

    /**
     * Перечень услуг
     */
   var services: List<BillServiceData> = ArrayList()
)
