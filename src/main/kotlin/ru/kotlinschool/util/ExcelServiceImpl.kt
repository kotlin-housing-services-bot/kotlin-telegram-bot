package ru.kotlinschool.util

import org.springframework.stereotype.Service
import ru.kotlinschool.dto.BillData

@Service
class ExcelServiceImpl : ExcelService {
    override fun build(data: BillData): ByteArray {
        TODO("Not yet implemented")
    }
}
