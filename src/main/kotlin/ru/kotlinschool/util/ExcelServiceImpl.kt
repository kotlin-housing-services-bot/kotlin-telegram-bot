package ru.kotlinschool.util

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kotlinschool.data.BillData

@Service
class ExcelServiceImpl @Autowired constructor(
    private val builder: ExcelBuilder
) : ExcelService {

    /***
     * Подготавливаем excel файл
     */
    override fun build(data: BillData): ByteArray {
        return builder.data(data).build()
    }

}
