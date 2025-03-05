package com.ll.TeamProject.global.scheduler;

import com.ll.TeamProject.domain.user.service.UserDormantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerConfig {
    private final UserDormantService userDormantService;

//    @Scheduled(cron = "0 0 10 1 * ?")
    public void processDormantAccounts() {
        userDormantService.processDormant();
    }
}
