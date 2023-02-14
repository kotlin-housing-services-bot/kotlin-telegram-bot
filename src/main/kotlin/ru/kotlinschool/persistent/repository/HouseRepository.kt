package ru.kotlinschool.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.kotlinschool.persistent.entity.House

@Repository
interface HouseRepository : JpaRepository<House, Long>{
    /**
     * Найти дома УК по ид УК
     */
    @Query("select t from House t join t.managementCompany mc where mc.id = :managementCompanyId")
    fun findHousesByManagementCompany(@Param("managementCompanyId") managementCompanyId: Long): List<House>

    /**
     * Найти дома УК по ид админа
     */
    @Query("select t from House t join t.managementCompany mc where mc.userId = :adminId")
    fun findHousesByAdminId(@Param("adminId") adminId: Long): List<House>
}

