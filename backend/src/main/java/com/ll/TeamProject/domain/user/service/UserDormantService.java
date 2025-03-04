package com.ll.TeamProject.domain.user.service;

import com.ll.TeamProject.domain.user.dto.DormantAccountProjection;
import com.ll.TeamProject.domain.user.repository.AuthenticationRepository;
import com.ll.TeamProject.domain.user.repository.UserRepository;
import com.ll.TeamProject.global.mail.GoogleMailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class UserDormantService {

    private final AuthenticationRepository authenticationRepository;
    private final GoogleMailService emailService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional
    public void processDormant() {
        // 기준 날짜 설정 (매월 1일 실행)
        YearMonth currentMonth = YearMonth.now();

        // 1. 휴면 안내 메일 전송 (닉네임과 이메일만 조회)
        List<DormantAccountProjection> notifyCandidates = findCandidatesByMonthsAgo(11);
        sendDormantNotificationEmail(notifyCandidates, currentMonth.plusMonths(1));

        // 2. 계정 잠금 (id 만 조회 후 한번에 100명씩 처리)
        List<Long> lockIds = findUserIdsByMonthsAgo(12);
        processInBatches(lockIds, 100, userRepository::bulkLockAccounts);

        // 3. 삭제 처리 (id 만 조회 후 한번에 100명씩 처리)
        List<Long> deleteIds = findUserIdsByMonthsAgo(18);
        processInBatches(deleteIds, 100,
                ids -> userRepository.bulkDeleteAccounts(ids, LocalDateTime.now()));
    }

    private List<DormantAccountProjection> findCandidatesByMonthsAgo(int monthsAgo) {
        LocalDateTime[] dateRange = calculateDateRange(monthsAgo);
        return authenticationRepository.findDormantAccountsInDateRange(dateRange[0], dateRange[1]);
    }

    private List<Long> findUserIdsByMonthsAgo(int monthsAgo) {
        LocalDateTime[] dateRange = calculateDateRange(monthsAgo);
        return userRepository.findUserIdsInDateRange(dateRange[0], dateRange[1]);
    }

    private LocalDateTime[] calculateDateRange(int monthsAgo) {
        YearMonth targetMonth = YearMonth.now().minusMonths(monthsAgo);
        LocalDateTime startDate = targetMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = targetMonth.atEndOfMonth().atTime(LocalTime.MAX);
        return new LocalDateTime[] { startDate, endDate };
    }

    private void sendDormantNotificationEmail(List<DormantAccountProjection> candidates, YearMonth nextMonth) {
        LocalDate nextMonthDate = nextMonth.atDay(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");

        candidates.forEach(candidate -> {
            String message = """
                    장기 미사용 이용자로 %s님 계정이 %s 휴면계정으로 전환될 예정입니다.
                    """.formatted(candidate.getNickname(), nextMonthDate.format(formatter));
            emailService.sendMail(candidate.getEmail(), "CanBeJ 휴면계정 전환 안내", message);
        });
    }

    private <T> void processInBatches(List<T> ids, int batchSize, Consumer<List<T>> processor) {
        for (int i = 0; i < ids.size(); i += batchSize) {
            int end = Math.min(i + batchSize, ids.size());
            List<T> batch = ids.subList(i, end);
            processor.accept(batch);
        }
    }
}
