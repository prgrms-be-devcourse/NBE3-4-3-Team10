package com.ll.TeamProject.global.scheduler

import com.ll.TeamProject.domain.user.service.UserDormantService
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SchedulerConfig(
    private val userDormantService: UserDormantService
) {
    @Scheduled(cron = "0 0 10 1 * ?") // 매월 1일 10시 실행
    fun processDormantAccounts() = runBlocking {
        userDormantService.processDormant()
    }
}