package com.ll.TeamProject.domain.chat.chat.dto

import java.time.LocalDateTime

data class ChatMessageDto(
    val senderId: Long,
    val calendarId: Long,
    val message: String = "",
    val sentAt:LocalDateTime = LocalDateTime.now()
)
