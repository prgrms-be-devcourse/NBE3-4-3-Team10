package com.ll.TeamProject.global.initData;

import com.ll.TeamProject.domain.calendar.entity.Calendar;
import com.ll.TeamProject.domain.calendar.repository.CalendarRepository;
import com.ll.TeamProject.domain.user.entity.Authentication;
import com.ll.TeamProject.domain.user.entity.ForbiddenNickname;
import com.ll.TeamProject.domain.user.entity.SiteUser;
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
                SiteUser admin = SiteUser
                        .builder()
                        .username("admin" + i)
                        .nickname("관리자" + i)
                        .password(passwordEncoder.encode("admin" + i))
                        .role(ADMIN)
                        .email("test"  + i + "@test.com")
                        .apiKey(UUID.randomUUID().toString())
                        .locked(false)
                        .build();
                admin = userRepository.save(admin);

                Authentication authentication = Authentication
                        .builder()
                        .user(admin)
                        .authType(LOCAL)
                        .failedAttempts(0)
                        .build();

                authenticationRepository.save(authentication);
            }

            for (int i = 1; i <= 13; i++) {
                SiteUser user = SiteUser.builder()
                        .username("user" + i)
                        .nickname("테스트 회원" + i)
                        .password(passwordEncoder.encode("1234"))
                        .role(USER)
                        .email("user" + i + "@test.com")
                        .apiKey(UUID.randomUUID().toString())
                        .locked(false)
                        .build();
                user = userRepository.save(user);

                Authentication userAuthentication = Authentication
                        .builder()
                        .user(user)
                        .authType(LOCAL)
                        .failedAttempts(0)
                        .build();

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

                SiteUser siteUser = SiteUser.builder()
                        .username(username)
                        .nickname(nickname)
                        .password(passwordEncoder.encode("1234"))
                        .role(USER)
                        .email(username + "@test.com")
                        .apiKey(UUID.randomUUID().toString())
                        .locked(false)
                        .build();
                userRepository.save(siteUser);

                Authentication authentication = Authentication.builder()
                        .user(siteUser)
                        .authType(LOCAL)
                        .lastLogin(LocalDateTime.now().minusMonths(monthsAgo))
                        .failedAttempts(0)
                        .build();
                authenticationRepository.save(authentication);
            }
        }
    }

    @Transactional
    public void makeDeletedUsers() {
        if (userRepository.count() < 20) {
            for (int i = 1; i <= 3; i++) {
                SiteUser siteUser = SiteUser.builder()
                        .username("deleted_" + UUID.randomUUID())
                        .nickname("탈퇴한 사용자"+ i)
                        .password(passwordEncoder.encode("1234"))
                        .role(USER)
                        .email("deleted_" + i + "@test.com")
                        .apiKey(UUID.randomUUID().toString())
                        .isDeleted(true)
                        .deletedDate(LocalDateTime.now())
                        .build();
                userRepository.save(siteUser);

                Authentication authentication = Authentication.builder()
                        .user(siteUser)
                        .authType(LOCAL)
                        .lastLogin(LocalDateTime.now().minusMonths(12))
                        .failedAttempts(0)
                        .build();
                authenticationRepository.save(authentication);
            }
        }
    }
}
