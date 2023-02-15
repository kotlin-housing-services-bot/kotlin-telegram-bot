package ru.kotlinschool.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.kotlinschool.persistent.entity.ManagementCompany

@Repository
interface ManagementCompanyRepository : JpaRepository<ManagementCompany, Long> {
    /**
     * Найти УК по пользователю telegram
     */
    fun findByUserId(userId: Long): ManagementCompany?

}
