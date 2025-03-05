package com.ll.TeamProject.global.initData;

import com.ll.TeamProject.domain.calendar.entity.Calendar;
import com.ll.TeamProject.domain.calendar.repository.CalendarRepository;
import com.ll.TeamProject.domain.user.entity.Authentication;
import com.ll.TeamProject.domain.user.entity.ForbiddenNickname;
import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.domain.user.enums.AuthType;
import com.ll.TeamProject.domain.user.repository.AuthenticationRepository;
import com.ll.TeamProject.domain.user.repository.ForbiddenRepository;
import com.ll.TeamProject.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ll.TeamProject.domain.user.enums.AuthType.LOCAL;
import static com.ll.TeamProject.domain.user.enums.Role.ADMIN;
import static com.ll.TeamProject.domain.user.enums.Role.USER;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationRepository authenticationRepository;
    private final ForbiddenRepository forbiddenRepository;

    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.makeSampleUsers();
            self.makeSampleCalenders();
            self.makeForbiddenNicknames();
            self.makeDormantUsers();
            self.makeDeletedUsers();
        };
    }
    @Transactional
    public void makeSampleUsers() {
        // 관리자 계정 만들기
        if (userRepository.count() == 0) {
            for(int i = 1 ; i <= 2 ; i++) {
                SiteUser admin = new SiteUser(
                        "admin" + i,
                        passwordEncoder.encode("admin" + i),
                        "관리자" + i,
                        "admin" + i + "@test.com",
                        ADMIN,
                        UUID.randomUUID().toString()
                );
                admin = userRepository.save(admin);

                Authentication authentication = Authentication.create(
                        admin,
                        LOCAL,
                        null,
                        0
                );

                authenticationRepository.save(authentication);
            }

            for (int i = 1; i <= 13; i++) {
                SiteUser user = new SiteUser(
                        "user" + i,
                        passwordEncoder.encode("1234"),
                        "회원" + i,
                        "user" + i + "@test.com",
                        USER,
                        UUID.randomUUID().toString()
                );

                user = userRepository.save(user);

                Authentication userAuthentication = Authentication.create(
                        user,
                        AuthType.LOCAL,
                        null,
                        0
                );

                authenticationRepository.save(userAuthentication);
            }
        }
    }

    @Transactional
    public void makeSampleCalenders() {
        if (calendarRepository.count() == 0) {
            // 각 사용자별 생성할 캘린더 수 정의
            Map<String, Integer> userCalendarCounts = Map.of(
                    "user2", 5,
                    "user3", 4,
                    "user4", 2,
                    "user5", 1
            );

            // 각 사용자별로 캘린더 생성
            userCalendarCounts.forEach((username, count) -> {
                SiteUser user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new IllegalStateException(username + "를 찾을 수 없습니다."));

                for (int i = 1; i <= count; i++) {
                    Calendar calendar = new Calendar(
                            user,
                            username + "의 캘린더 " + i,
                            username + "의 " + i + "번째 테스트 캘린더입니다."
                    );
                    calendarRepository.save(calendar);
                }
            });
        }
    }

    @Transactional
    public void makeForbiddenNicknames() {
        if (forbiddenRepository.count() == 0) {

            String[] forbiddenNames = {
                    "어?", "404", "200", "500", "null",
                    "DROP TABLE Site_User", "rm -rf", "undefined",
                    "git push origin main --force", "NullPointerException",
                    "sudo", "localhost", "test", "guest",
                    "admin", "error", "exception", "deprecated",
                    "Kakao", "Google"
            };

            for (String name : forbiddenNames) {
                ForbiddenNickname forbiddenNickname = new ForbiddenNickname(name);
                forbiddenRepository.save(forbiddenNickname);
            }
        }
    }

    @Transactional
    public void makeDormantUsers() {
        if (userRepository.count() < 16) {
            List<Integer> loginMonths = List.of(11, 12, 17, 18);

            for (int monthsAgo : loginMonths) {
                String username = "login_" + monthsAgo + "_months_ago";
                String nickname = monthsAgo + "개월전";

                SiteUser user = new SiteUser(
                        username,
                        passwordEncoder.encode("1234"),
                        nickname,
                        username + "@test.com",
                        USER,
                        UUID.randomUUID().toString()
                );

                userRepository.save(user);

                Authentication authentication = Authentication.create(
                        user,
                        AuthType.LOCAL,
                        LocalDateTime.now().minusMonths(monthsAgo),
                        0
                );
                authenticationRepository.save(authentication);
            }
        }
    }

    @Transactional
    public void makeDeletedUsers() {
        if (userRepository.count() < 20) {
            for (int i = 1; i <= 3; i++) {

                SiteUser user = new SiteUser(
                        "deleted_" + UUID.randomUUID().toString(),
                        passwordEncoder.encode("1234"),
                        "탈퇴회원" + i,
                        "deleted_" + i + "@test.com",
                        USER,
                        UUID.randomUUID().toString(),
                        true,
                        LocalDateTime.now()
                );
                userRepository.save(user);

                Authentication authentication = Authentication.create(
                        user,
                        AuthType.LOCAL,
                        LocalDateTime.now().minusMonths(12),
                        0
                );
                authenticationRepository.save(authentication);
            }
        }
    }
}
