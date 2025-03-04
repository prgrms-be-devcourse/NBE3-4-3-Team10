package com.ll.TeamProject.domain.user.entity

import com.ll.TeamProject.domain.user.enums.AuthType
import com.ll.TeamProject.domain.user.enums.Role
import com.ll.TeamProject.global.jpa.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Authentication(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: SiteUser,

    @Enumerated(EnumType.STRING)
    val authType: AuthType,

    var lastLogin: LocalDateTime? = null,

    var failedAttempts: Int = 0,

    var isLocked: Boolean = false
) : BaseEntity() {
    protected constructor() : this(SiteUser("", "", "", "", Role.USER), AuthType.LOCAL)

    fun updateLastLogin() {
        lastLogin = LocalDateTime.now()
    }

    fun incrementFailedAttempts(): Int {
        return ++failedAttempts
    }

    fun resetFailedAttempts() {
        failedAttempts = 0
    }

    companion object {
        @JvmStatic // TODO: 테스트 코드에서 필요
        fun create(user: SiteUser, authType: AuthType, lastLogin: LocalDateTime? = null, failedAttempts: Int = 0): Authentication {
            return Authentication(user, authType, lastLogin, failedAttempts, false)
        }
    }
}

