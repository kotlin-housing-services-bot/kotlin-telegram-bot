package ru.kotlinschool.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.kotlinschool.persistent.entity.Flat

@Repository
interface FlatRepository : JpaRepository<Flat, Long> {
    /**
     * Определить квартиры по ид пользователя Telegram
     */
    fun findByUserId(userId: Long): List<Flat>

}
