package ru.kotlinschool.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.kotlinschool.persistent.entity.PublicService

@Repository
interface ServiceRepository : JpaRepository<PublicService, Long> {
}