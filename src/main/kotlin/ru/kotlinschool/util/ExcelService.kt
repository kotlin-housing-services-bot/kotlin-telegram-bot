package ru.kotlinschool.util

import ru.kotlinschool.data.BillData

interface ExcelService {

    fun build(data: BillData): ByteArray

}
