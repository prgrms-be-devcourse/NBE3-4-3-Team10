package com.ll.TeamProject.domain.user.service

import com.ll.TeamProject.domain.user.dto.LoginDto
import com.ll.TeamProject.domain.user.dto.UserDto
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.exceptions.UserErrorCode
import com.ll.TeamProject.domain.user.repository.UserRepository
import com.ll.TeamProject.global.exceptions.CustomException
import com.ll.TeamProject.global.userContext.UserContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val authenticationService: AuthenticationService,
    private val userContext: UserContext,
    private val passwordEncoder: PasswordEncoder
) {

    fun login(username: String, password: String): LoginDto {
        val user = userRepository.findByUsername(username)
            .orElseThrow { CustomException(UserErrorCode.INVALID_CREDENTIALS) }

        if (user.isLocked()) throw CustomException(UserErrorCode.ACCOUNT_LOCKED)

        if (!passwordEncoder.matches(password, user.password)) {
            authenticationService.handleLoginFailure(user)
            throw CustomException(UserErrorCode.INVALID_CREDENTIALS)
        }

        return createLoginResponse(user)
    }

    private fun createLoginResponse(user: SiteUser): LoginDto {
        authenticationService.modifyLastLogin(user)
        val accessToken = userContext.makeAuthCookies(user)
        return LoginDto(UserDto(user), user.apiKey, accessToken)
    }

    fun logout() {
        SecurityContextHolder.clearContext()
    }
}
