package com.ll.TeamProject.global.initData

import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.calendar.repository.CalendarRepository
import com.ll.TeamProject.domain.user.entity.Authentication.Companion.create
import com.ll.TeamProject.domain.user.entity.ForbiddenNickname
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.enums.AuthType
import com.ll.TeamProject.domain.user.enums.Role
import com.ll.TeamProject.domain.user.repository.AuthenticationRepository
import com.ll.TeamProject.domain.user.repository.ForbiddenRepository
import com.ll.TeamProject.domain.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.util.*

@Configuration
class BaseInitData(
    private val calendarRepository: CalendarRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationRepository: AuthenticationRepository,
    private val forbiddenRepository: ForbiddenRepository,
    @Lazy private val self: BaseInitData
) {
    @Bean
    fun baseInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner { _: ApplicationArguments? ->
            self.makeSampleUsers()
            self.makeSampleCalendars()
            self.makeForbiddenNicknames()
            self.makeDormantUsers()
            self.makeDeletedUsers()
        }
    }

    @Transactional
    fun makeSampleUsers() {
        // 관리자 계정 만들기
        if (userRepository.count() == 0L) {
            repeat(2) { i ->
                val admin = SiteUser(
                    "admin${i + 1}",
                    passwordEncoder.encode("admin${i + 1}"),
                    "관리자${i + 1}",
                    "admin${i + 1}@test.com",
                    Role.ADMIN,
                    UUID.randomUUID().toString()
                )
                userRepository.save(admin)

                val authentication = create(admin, AuthType.LOCAL, null, 0)
                authenticationRepository.save(authentication)
            }

            repeat(13) { i ->
                val user = SiteUser(
                    "user${i + 1}",
                    passwordEncoder.encode("1234"),
                    "회원${i + 1}",
                    "user${i + 1}@test.com",
                    Role.USER,
                    UUID.randomUUID().toString()
                )
                userRepository.save(user)

                val userAuthentication = create(user, AuthType.LOCAL, null, 0)
                authenticationRepository.save(userAuthentication)
            }
        }
    }

    @Transactional
    fun makeSampleCalendars() {
        if (calendarRepository.count() == 0L) {
            // 각 사용자별 생성할 캘린더 수 정의
            val userCalendarCounts = mapOf(
                "user2" to 5,
                "user3" to 4,
                "user4" to 2,
                "user5" to 1
            )

            // 각 사용자별로 캘린더 생성
            userCalendarCounts.forEach { (username, count) ->
                val user = userRepository.findByUsername(username)
                    .orElseThrow { IllegalStateException("$username 를 찾을 수 없습니다.") }

                repeat(count) { i ->
                    val calendar = Calendar(
                        user,
                        "$username 의 캘린더 ${i + 1}",
                        "$username 의 ${i + 1}번째 테스트 캘린더입니다."
                    )
                    calendarRepository.save(calendar)
                }
            }
        }
    }

    @Transactional
    fun makeForbiddenNicknames() {
        if (forbiddenRepository.count() == 0L) {
            val forbiddenNames = listOf(
                "어?", "404", "200", "500", "null",
                "DROP TABLE Site_User", "rm -rf", "undefined",
                "git push origin main --force", "NullPointerException",
                "sudo", "localhost", "test", "guest",
                "admin", "error", "exception", "deprecated",
                "Kakao", "Google"
            )

            forbiddenNames.forEach { name ->
                val forbiddenNickname = ForbiddenNickname(name)
                forbiddenRepository.save(forbiddenNickname)
            }
        }
    }

    @Transactional
    fun makeDormantUsers() {
        if (userRepository.count() < 16) {
            val loginMonths = listOf(11, 12, 17, 18)

            loginMonths.forEach { monthsAgo ->
                val username = "login_${monthsAgo}_months_ago"
                val nickname = "${monthsAgo}개월전"

                val user = SiteUser(
                    username,
                    passwordEncoder.encode("1234"),
                    nickname,
                    "$username@test.com",
                    Role.USER,
                    UUID.randomUUID().toString()
                )

                userRepository.save(user)

                val authentication = create(
                    user,
                    AuthType.LOCAL,
                    LocalDateTime.now().minusMonths(monthsAgo.toLong()),
                    0
                )
                authenticationRepository.save(authentication)
            }
        }
    }

    @Transactional
    fun makeDeletedUsers() {
        if (userRepository.count() < 20) {
            repeat(3) { i ->
                val user = SiteUser(
                    "deleted_${UUID.randomUUID()}",
                    passwordEncoder.encode("1234"),
                    "탈퇴회원${i + 1}",
                    "deleted_${i + 1}@test.com",
                    Role.USER,
                    UUID.randomUUID().toString(),
                    true,
                    LocalDateTime.now()
                )
                userRepository.save(user)

                val authentication = create(
                    user,
                    AuthType.LOCAL,
                    LocalDateTime.now().minusMonths(12),
                    0
                )
                authenticationRepository.save(authentication)
            }
        }
    }
}
