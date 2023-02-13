package ru.kotlinschool.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.kotlinschool.persistent.entity.Flat
import ru.kotlinschool.persistent.entity.Metric
import ru.kotlinschool.persistent.entity.PublicService

@Repository
interface MetricRepository : JpaRepository<Metric, Long> {

    /**
     * Найти показания по квартире и услуге
     */
    fun findByFlatAndPublicService(flat: Flat, service: PublicService): List<Metric>

    /**
     * Найти последние показания
     */
    fun findFirstByFlatAndPublicServiceOrderByActionDateDesc(flat: Flat, service: PublicService): Metric?

}
