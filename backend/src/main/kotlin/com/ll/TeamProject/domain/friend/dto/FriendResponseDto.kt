package com.ll.TeamProject.domain.friend.dto

import com.ll.TeamProject.domain.friend.entity.Friendship
import com.ll.TeamProject.domain.friend.entity.FriendshipStatus
import com.ll.TeamProject.domain.user.dto.UserDto
import com.ll.TeamProject.domain.user.mapper.UserMapper

data class FriendResponseDto(
    val friend: UserDto,
    val status: FriendshipStatus
) {
    companion object {
        fun from(friendship: Friendship, currentUserId: Long): FriendResponseDto {
            val friend = if (friendship.user1.id == currentUserId) friendship.user2 else friendship.user1
            return FriendResponseDto(
                friend = UserMapper.toDto(friend), // ✅ 변경된 부분
                status = friendship.status
            )
        }
    }
}
