package com.ll.TeamProject.domain.friend.dto

import com.ll.TeamProject.domain.friend.entity.Friendship
import com.ll.TeamProject.domain.user.dto.UserDto
import com.ll.TeamProject.domain.user.mapper.UserMapper

data class FriendRequestDto(
    val id: Long,       // 친구 요청 ID
    val sender: UserDto // 친구 요청을 보낸 사람 정보
) {
    companion object {
        fun from(friendship: Friendship): FriendRequestDto {
            return FriendRequestDto(
                id = friendship.id!!,
                sender = UserMapper.toDto(friendship.user1) // 요청을 보낸 사람
            )
        }
    }
}
