package com.ll.TeamProject.domain.user.service

import com.ll.TeamProject.domain.user.entity.Authentication.Companion.create
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.enums.AuthType
import com.ll.TeamProject.domain.user.enums.Role
import com.ll.TeamProject.domain.user.repository.AuthenticationRepository
import com.ll.TeamProject.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class JoinService(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository
) {

    fun findOrRegisterUser(username: String, email: String, providerTypeCode: String): SiteUser {
        return userRepository.findByUsername(username)
            .or { userRepository.findByEmail(email) }
            .orElseGet { join(username, "", email, providerTypeCode) }
    }

    fun join(username: String, password: String, email: String, providerTypeCode: String): SiteUser {
        var user = SiteUser(
            username,
            password,
            username,
            email,
            Role.USER,
            UUID.randomUUID().toString()
        )
        user = userRepository.save(user)

        val authType = AuthType.valueOf(providerTypeCode)
        val authentication = create(
            user,
            authType,
            null,
            0
        )
        authenticationRepository.save(authentication)

        return user
    }
}
