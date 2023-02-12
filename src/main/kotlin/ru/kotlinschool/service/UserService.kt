package ru.kotlinschool.service

interface UserService {

    fun registerFlat(userId: Long, houseId: Long, flatNumber: String, area: Double, numberOfResidents: Int)

}
