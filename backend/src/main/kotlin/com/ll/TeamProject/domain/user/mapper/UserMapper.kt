package com.ll.TeamProject.domain.user.mapper

import com.ll.TeamProject.domain.user.dto.UserDto
import com.ll.TeamProject.domain.user.entity.SiteUser

object UserMapper {
    fun toDto(user: SiteUser): UserDto {
        return UserDto(
            id = user.id!!,
            username = user.username,
            nickname = user.nickname,
            email = user.email,
            createDate = user.createDate,
            modifyDate = user.modifyDate,
            locked = user.locked
        )
    }
}
