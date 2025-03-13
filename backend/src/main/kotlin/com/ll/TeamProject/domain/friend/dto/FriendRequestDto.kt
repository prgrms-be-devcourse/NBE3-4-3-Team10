package com.ll.TeamProject.domain.friend.dto

data class FriendRequestDto(
    val senderId: Long,  // 요청 보낸 사용자 ID
    val receiverNickname: String // 요청 받는 사용자 닉네임
)