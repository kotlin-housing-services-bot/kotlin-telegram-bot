package ru.kotlinschool.util

import ru.kotlinschool.dto.BillData

interface ExcelService {

    fun build(data: BillData): ByteArray

}
