package com.ll.TeamProject.domain.user.service;

import com.ll.TeamProject.domain.user.dto.DormantAccountProjection;
import com.ll.TeamProject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class UserDormantServiceTest {

    @Autowired
    private UserDormantService userDormantService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void notifyCandidates_test() {
        System.out.println("= = = notifyCandidates test = = =");

        List<DormantAccountProjection> notifyCandidates = userDormantService.findCandidatesByMonthsAgo(11);
        assertEquals(1, notifyCandidates.size());
    }

    @Test
    void lockIds_test() {
        System.out.println("= = = lockIds test = = =");

        List<Long> lockIds = userDormantService.findUserIdsByMonthsAgo(12);
        assertEquals(1, lockIds.size());

        userDormantService.processInBatches(lockIds, 100, userRepository::bulkLockAccounts);

        assertTrue(userService.findById(lockIds.getFirst()).get().isLocked());
    }

    @Test
    void deleteIds_test() {
        System.out.println("= = = deleteIds test = = =");

        List<Long> deleteIds = userDormantService.findUserIdsByMonthsAgo(18);
        assertEquals(1, deleteIds.size());

        userDormantService.processInBatches(deleteIds, 100,
                ids -> userRepository.bulkDeleteAccounts(ids, LocalDateTime.now()));

        assertTrue(userService.findById(deleteIds.getFirst()).get().isDeleted());
    }
}
