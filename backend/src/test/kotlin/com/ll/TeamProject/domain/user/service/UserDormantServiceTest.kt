package com.ll.TeamProject.domain.user.service

import com.ll.TeamProject.domain.user.repository.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
internal class UserDormantServiceTest {
    @Autowired
    private lateinit var userDormantService: UserDormantService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun notifyCandidates_test() {
        println("= = = notifyCandidates test = = =")

        val notifyCandidates = userDormantService.findCandidatesByMonthsAgo(11)
        Assertions.assertEquals(1, notifyCandidates.size)
    }

    @Test
    fun lockIds_test() {
        println("= = = lockIds test = = =")

        val lockIds: List<Long> = userDormantService.findUserIdsByMonthsAgo(12)
        Assertions.assertEquals(1, lockIds.size)

        userDormantService.processInBatches(
            lockIds, 100
        ) { ids: List<Long> -> userRepository.bulkLockAccounts(ids) }

        Assertions.assertTrue(userService.findById(lockIds.first()).get().isLocked())
    }

    @Test
    fun deleteIds_test() {
        println("= = = deleteIds test = = =")

        val deleteIds: List<Long> = userDormantService.findUserIdsByMonthsAgo(18)
        Assertions.assertEquals(1, deleteIds.size)

        userDormantService.processInBatches(
            deleteIds, 100
        ) { ids: List<Long> -> userRepository.bulkDeleteAccounts(ids, LocalDateTime.now()) }

        Assertions.assertTrue(userService.findById(deleteIds.first()).get().isDeleted)
    }
}
