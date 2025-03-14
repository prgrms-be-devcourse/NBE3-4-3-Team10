package com.ll.TeamProject.domain.user.service

import com.ll.TeamProject.domain.user.dto.DormantAccountProjection
import com.ll.TeamProject.domain.user.repository.AuthenticationRepository
import com.ll.TeamProject.domain.user.repository.UserRepository
import com.ll.TeamProject.global.mail.GoogleMailService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.function.Consumer
import kotlin.math.min

@Service
class UserDormantService(
    private val authenticationRepository: AuthenticationRepository,
    private val emailService: GoogleMailService,
    private val userRepository: UserRepository,
    private val transactionTemplate: TransactionTemplate
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    suspend fun processDormant() {
        // 기준 날짜 설정 (매월 1일 실행)
        val currentMonth = YearMonth.now()

        // 1. 휴면 안내 메일 전송 (닉네임과 이메일만 조회)
        val notifyCandidates = findCandidatesByMonthsAgo(11)
        coroutineScope.launch {
            sendDormantNotificationEmail(notifyCandidates, currentMonth.plusMonths(1))
        }

        // 2. 계정 잠금 (id 만 조회 후 한번에 100명씩 처리)
        lockDormantAccounts()

        // 3. 삭제 처리 (id 만 조회 후 한번에 100명씩 처리)
        deleteDormantAccounts()
    }

    private fun lockDormantAccounts() {
        val lockIds = findUserIdsByMonthsAgo(12)
        processInBatches(lockIds, 100) { ids ->
            transactionTemplate.execute {
                userRepository.bulkLockAccounts(ids)
            }
        }
    }

    private fun deleteDormantAccounts() {
        val deleteIds = findUserIdsByMonthsAgo(18)
        processInBatches(deleteIds, 100) { ids ->
            transactionTemplate.execute {
                userRepository.bulkDeleteAccounts(ids, LocalDateTime.now())
            }
        }
    }

    fun findCandidatesByMonthsAgo(monthsAgo: Int): List<DormantAccountProjection> {
        val dateRange = calculateDateRange(monthsAgo)
        return authenticationRepository.findDormantAccountsInDateRange(dateRange[0], dateRange[1])
    }

    fun findUserIdsByMonthsAgo(monthsAgo: Int): List<Long> {
        val dateRange = calculateDateRange(monthsAgo)
        return userRepository.findUserIdsInDateRange(dateRange[0], dateRange[1])
    }

    private fun calculateDateRange(monthsAgo: Int): Array<LocalDateTime> {
        val targetMonth = YearMonth.now().minusMonths(monthsAgo.toLong())
        val startDate = targetMonth.atDay(1).atStartOfDay()
        val endDate = targetMonth.atEndOfMonth().atTime(LocalTime.MAX)
        return arrayOf(startDate, endDate)
    }

    private fun sendDormantNotificationEmail(candidates: List<DormantAccountProjection>, nextMonth: YearMonth) {
        val nextMonthDate = nextMonth.atDay(1)
        val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")

        coroutineScope.launch {
            candidates.forEach { candidate ->
                launch {
                    val message = """
                    안녕하세요, ${candidate.nickname} 님.
                    장기 미사용 이용자로 ${candidate.nickname} 님 계정이 ${nextMonthDate.format(formatter)} 휴면계정으로 전환될 예정입니다.
                """.trimIndent()

                    emailService.sendMail(candidate.email, "CanBeJ 휴면계정 전환 안내", message)
                }
            }
        }
    }

    fun <T> processInBatches(ids: List<T>, batchSize: Int, processor: Consumer<List<T>>) {
        var i = 0
        while (i < ids.size) {
            val end = min((i + batchSize).toDouble(), ids.size.toDouble()).toInt()
            val batch = ids.subList(i, end)
            processor.accept(batch)
            i += batchSize
        }
    }
}
