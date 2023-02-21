package ru.kotlinschool.util

fun generateBillName(address: String, month: Int, year: Int): String {
    return "Платеж по $address за $month:$year.xlsx";
}
