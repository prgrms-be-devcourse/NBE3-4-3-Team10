package com.ll.TeamProject.domain.friend.dto

import com.ll.TeamProject.domain.friend.entity.Friendship
import com.ll.TeamProject.domain.friend.entity.FriendshipStatus
import com.ll.TeamProject.domain.user.dto.UserDto
import com.ll.TeamProject.domain.user.mapper.UserMapper

data class FriendRequestDto(
    val id: Long,
    val sender: UserDto,  // 요청 보낸 사용자
    val receiver: UserDto, // 요청 받은 사용자
    val status: FriendshipStatus
) {
    companion object {
        fun from(friendship: Friendship): FriendRequestDto {
            val senderDto = UserMapper.toDto(friendship.user1) // ✅ 매핑 최소화
            val receiverDto = UserMapper.toDto(friendship.user2)

            return FriendRequestDto(
                id = friendship.id!!,
                sender = senderDto,
                receiver = receiverDto,
                status = friendship.status
            )
        }
    }
}
