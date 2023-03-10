package ru.kotlinschool.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.kotlinschool.persistent.entity.Flat
import ru.kotlinschool.persistent.entity.ManagementCompany
import ru.kotlinschool.persistent.entity.Metric
import ru.kotlinschool.persistent.entity.PublicService
import ru.kotlinschool.persistent.entity.Rate

@Repository
interface RateRepository : JpaRepository<Rate, Long>{

    /**
     * Найти актуальный тариф
     */
    fun findFirstByPublicServiceOrderByDateBeginDesc(service: PublicService): Rate?
}
