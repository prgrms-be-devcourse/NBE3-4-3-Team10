package com.ll.TeamProject.domain.user.dto

import com.ll.TeamProject.domain.user.entity.SiteUser
import java.time.LocalDateTime

data class UserDto(
    val id: Long,
    val username: String,
    val nickname: String,
    val email: String,
    val createDate: LocalDateTime?,
    val modifyDate: LocalDateTime?,
    val locked: Boolean
) {
    constructor(user: SiteUser) : this(
        id = user.id!!,
        username = user.username,
        nickname = user.nickname,
        email = user.email,
        createDate = user.createDate,
        modifyDate = user.modifyDate,
        locked = user.locked
    )

    companion object {
        fun from(user: SiteUser): UserDto {
            return UserDto(user)
        }
    }
}
