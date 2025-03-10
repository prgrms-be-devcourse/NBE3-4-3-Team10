package com.ll.TeamProject.domain.friend.dto

import com.ll.TeamProject.domain.user.entity.SiteUser

data class FriendResponseDto(
    val id: Long,
    val username: String,
    val email: String
) {
    companion object {
        fun from(user: SiteUser): FriendResponseDto {
            return FriendResponseDto(
                id = user.id!!,
                username = user.username,
                email = user.email
            )
        }
    }
}
