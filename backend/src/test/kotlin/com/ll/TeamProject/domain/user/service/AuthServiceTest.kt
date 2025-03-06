package com.ll.TeamProject.domain.user.service

import com.ll.TeamProject.domain.user.exceptions.UserErrorCode
import com.ll.TeamProject.domain.user.repository.UserRepository
import com.ll.TeamProject.global.exceptions.CustomException
import com.ll.TeamProject.global.userContext.UserContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class AuthServiceTest {
    @InjectMocks
    private lateinit var authService: AuthService

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var authenticationService: AuthenticationService

    @Mock
    private lateinit var userContext: UserContext

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    @DisplayName("로그인시 잘못된 아이디")
    fun t1() {
        // given
        val username = "nonexistentUser"
        val password = "password"

        Mockito.`when`(userRepository.findByUsername(username)).thenReturn(Optional.empty())

        // when & then
        val exception = Assertions.assertThrows(CustomException::class.java) {
            authService.login(username, password)
        }

        Assertions.assertEquals(UserErrorCode.INVALID_CREDENTIALS, exception.errorCode)
        Assertions.assertEquals(UserErrorCode.INVALID_CREDENTIALS.message, exception.message)
    }

    @Test
    @DisplayName("로그인시 잘못된 비밀번호")
    fun t2() {
        // given
        val username = "admin1"
        val password = "wrongPassword"

        Mockito.`when`(userRepository.findByUsername(username)).thenReturn(Optional.empty())

        // when & then
        val exception = Assertions.assertThrows(CustomException::class.java) {
            authService.login(username, password)
        }

        Assertions.assertEquals(UserErrorCode.INVALID_CREDENTIALS, exception.errorCode)
        Assertions.assertEquals(UserErrorCode.INVALID_CREDENTIALS.message, exception.message)
    }
}
