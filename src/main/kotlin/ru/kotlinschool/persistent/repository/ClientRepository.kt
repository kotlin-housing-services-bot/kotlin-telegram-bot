package ru.kotlinschool.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.kotlinschool.persistent.entity.Client

@Repository
interface ClientRepository : JpaRepository<Client, Long> {

    fun findClientByTelegramUserId(telegramUserId: Long): Client?
}
