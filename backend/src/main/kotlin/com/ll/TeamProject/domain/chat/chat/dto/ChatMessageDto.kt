package com.ll.TeamProject.domain.chat.chat.dto

data class ChatMessageDto(
    val senderId: Long,
    val calendarId: Long,
    val message: String = ""
)
