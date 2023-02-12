package ru.kotlinschool.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.kotlinschool.persistent.entity.Flat
import ru.kotlinschool.persistent.entity.MeterReading
import ru.kotlinschool.persistent.entity.PublicService

@Repository
interface MeterReadingRepository : JpaRepository<MeterReading, Long> {

    fun findByFlatAndPublicService(flat: Flat, service: PublicService): List<MeterReading>

    /**
     * Найти последние показания
     */
    fun findFirstByFlatAndPublicServiceOrderByActionDateDesc(flat: Flat, service: PublicService): MeterReading?

}
