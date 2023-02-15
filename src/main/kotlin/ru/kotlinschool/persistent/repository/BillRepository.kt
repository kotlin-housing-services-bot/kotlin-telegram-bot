package ru.kotlinschool.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.kotlinschool.persistent.entity.Bill

interface BillRepository : JpaRepository<Bill, Long>
