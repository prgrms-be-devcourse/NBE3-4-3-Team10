package com.ll.TeamProject.domain.user.service

import com.ll.TeamProject.domain.user.entity.Authentication
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.repository.AuthenticationRepository
import com.ll.TeamProject.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AuthenticationService(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository
) {

    fun modifyLastLogin(user: SiteUser) {
        authenticationRepository.findByUserId(user.id!!)
            .ifPresent { authentication: Authentication ->
                authentication.updateLastLogin()
                authentication.resetFailedAttempts()
                authenticationRepository.save(authentication)
            }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleLoginFailure(user: SiteUser) {
        authenticationRepository.findByUserId(user.id!!).ifPresent { authentication: Authentication ->
            val failedLogin = authentication.incrementFailedAttempts()
            if (failedLogin >= 5) {
                user.lockAccount()
                userRepository.save(user)
            }
            authenticationRepository.save(authentication)
        }
    }

    fun findByUserId(id: Long): Optional<Authentication> {
        return authenticationRepository.findByUserId(id)
    }
}
