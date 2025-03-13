package com.ll.TeamProject.domain.friend.dto

import com.ll.TeamProject.domain.friend.entity.Friendship
import com.ll.TeamProject.domain.friend.entity.FriendshipStatus
import com.ll.TeamProject.domain.user.dto.UserDto
import com.ll.TeamProject.domain.user.mapper.UserMapper

data class FriendResponseDto(
    val targetUser: UserDto, // ✅ 변수명 변경으로 가독성 향상
    val status: FriendshipStatus
) {
    companion object {
        fun from(friendship: Friendship, currentUserId: Long): FriendResponseDto {
            val targetUser = if (friendship.user1.id == currentUserId) friendship.user2 else friendship.user1
            val targetUserDto = UserMapper.toDto(targetUser) // ✅ 매핑 최소화

            return FriendResponseDto(
                targetUser = targetUserDto,
                status = friendship.status
            )
        }
    }
}
