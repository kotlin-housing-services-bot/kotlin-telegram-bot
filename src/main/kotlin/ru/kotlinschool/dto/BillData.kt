package ru.kotlinschool.dto

data class BillData(
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
    var area: Double? = null,

    /**
     * Количество прописанных
     */
    var numberOfResidents: Long? = null,

    /**
     * Перечень услуг
     */
   var services: List<BillServiceData> = ArrayList()
)
