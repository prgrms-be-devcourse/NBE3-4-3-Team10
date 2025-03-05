package com.ll.TeamProject.global.scheduler

import com.ll.TeamProject.domain.user.service.UserDormantService
import org.springframework.stereotype.Component

@Component
class SchedulerConfig(
    private val userDormantService: UserDormantService
) {

//    @Scheduled(cron = "0 0 10 1 * ?")
    fun processDormantAccounts() {
        userDormantService.processDormant()
    }
}
