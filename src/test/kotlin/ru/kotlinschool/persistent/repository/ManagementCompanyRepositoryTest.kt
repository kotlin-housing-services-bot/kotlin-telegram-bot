package ru.kotlinschool.persistent.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import ru.kotlinschool.persistent.entity.ManagementCompany

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ManagementCompanyRepositoryTest {

    @Autowired
    private lateinit var rep: ManagementCompanyRepository

    @Test
    fun findByUserIdSuccessTest() {
        // given
        val savedManagementCompany1 = rep.save(ManagementCompany(name = "УК", inn = "123456789111", userId = 1L))
        val savedManagementCompany2 = rep.save(ManagementCompany(name = "ТСЖ", inn = "222222222222", userId = 2L))

        // when
        val foundEntity = rep.findByUserId(1L)
        // then
        Assertions.assertTrue { foundEntity == savedManagementCompany1 }
    }
}