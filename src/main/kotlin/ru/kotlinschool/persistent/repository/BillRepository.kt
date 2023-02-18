package ru.kotlinschool.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.kotlinschool.persistent.entity.Bill

interface BillRepository : JpaRepository<Bill, Long>{
    /**
     * Найти квитанцию
     */
    @Query("select t from Bill t join t.flat f where f.id = :flatId and t.month = :month and t.year = :year")
    fun findBill(@Param("flatId") flatId: Long, @Param("year") year: Int,  @Param("month") month: Int): Bill?
}

