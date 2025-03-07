package com.ll.TeamProject.domain.chat.chat.repository

import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.chat.chat.entity.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {
    fun findByCalendar(calendar: Calendar): ChatRoom?
}
