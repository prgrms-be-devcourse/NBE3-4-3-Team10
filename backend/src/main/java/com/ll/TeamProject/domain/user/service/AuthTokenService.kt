package com.ll.TeamProject.domain.user.service

import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.enums.Role
import com.ll.TeamProject.standard.util.Jwt
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AuthTokenService {
    @Value("\${custom.jwt.secretKey}")
    private val secretKey: String = ""

    @Value("\${custom.accessToken.expirationSeconds}")
    private val accessTokenExpirationSeconds: Long = 0

    fun genAccessToken(user: SiteUser): String {
        val id = user.id!!
        val username = user.username
        val role = user.role.name
        val nickname = user.nickname

        return Jwt.toString(
            secretKey,
            accessTokenExpirationSeconds,
            java.util.Map.of("id", id, "username", username, "nickname", nickname, "role", role)
        )
    }

    fun payload(accessToken: String): Map<String, Any>? {
        val parsedPayload = Jwt.payload(secretKey, accessToken) ?: return null

        val id = (parsedPayload["id"] as Int).toLong()
        val username = parsedPayload["username"] as String
        val role = Role.valueOf((parsedPayload["role"] as String))
        val nickname = parsedPayload["nickname"] as String

        return java.util.Map.of<String, Any>(
            "id", id, "username", username, "role", role, "nickname", nickname)

    }
}
