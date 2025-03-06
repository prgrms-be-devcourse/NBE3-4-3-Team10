package com.ll.TeamProject.global.scheduler

import com.ll.TeamProject.domain.user.service.UserDormantService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SchedulerConfig(
    private val userDormantService: UserDormantService
) {
//    @Scheduled(cron = "0 0 10 1 * ?")
    @Scheduled(cron = "0 * * * * ?")
    fun processDormantAccounts() {
        CoroutineScope(Dispatchers.IO).launch {
            userDormantService.processDormant()
        }
    }
}
