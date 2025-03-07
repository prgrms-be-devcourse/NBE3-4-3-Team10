package com.ll.TeamProject.domain.chat.chat.repository

import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.chat.chat.entity.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository

interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {
    fun findByCalendar(calendar: Calendar): List<ChatMessage>
}
