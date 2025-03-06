package com.ll.TeamProject.standard.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.io.Serializable
import java.util.*
import javax.crypto.SecretKey

object Jwt {
    fun toString(secret: String, expireSeconds: Long, body: Map<String, out Serializable>): String {
        val secretKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

        val issuedAt = Date()
        val expiresAt = Date(issuedAt.time + expireSeconds * 1000)

        return Jwts.builder()
            .claims(body)
            .issuedAt(issuedAt)
            .expiration(expiresAt)
            .signWith(secretKey)
            .compact()
    }

    fun payload(secret: String, accessTokenStr: String): Map<String, Any>? {
        val secretKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

        return runCatching {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parse(accessTokenStr)
                .payload as Map<String, Any>
        }.getOrNull()
    }
}

