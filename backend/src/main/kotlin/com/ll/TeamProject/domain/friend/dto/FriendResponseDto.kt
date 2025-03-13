package com.ll.TeamProject.domain.friend.dto

import com.ll.TeamProject.domain.friend.entity.Friendship
import com.ll.TeamProject.domain.friend.entity.FriendshipStatus
import com.ll.TeamProject.domain.user.dto.UserDto

data class FriendResponseDto(
    val friend: UserDto,
    val status: FriendshipStatus
) {
    companion object {
        fun from(friendship: Friendship, currentUserId: Long): FriendResponseDto {
            val friend = if (friendship.user1.id == currentUserId) friendship.user2 else friendship.user1
            return FriendResponseDto(
                friend = UserDto.from(friend), // 친구의 정보
                status = friendship.status
            )
        }
    }
}
