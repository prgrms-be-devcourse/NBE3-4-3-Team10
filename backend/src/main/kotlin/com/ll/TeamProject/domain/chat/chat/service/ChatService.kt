package com.ll.TeamProject.domain.chat.chat.service

import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.chat.chat.dto.ChatMessageDto
import com.ll.TeamProject.domain.chat.chat.entity.ChatMessage
import com.ll.TeamProject.domain.chat.chat.repository.ChatMessageRepository
import com.ll.TeamProject.domain.user.entity.SiteUser
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val chatMessageRepository: ChatMessageRepository
) {

    fun saveMessage(sender: SiteUser, calendar: Calendar, dto: ChatMessageDto): ChatMessage {
        val message = ChatMessage(
            sender = sender,
            calendar = calendar,
            message = dto.message
        )
        return chatMessageRepository.save(message)
    }


    fun getMessagesByCalendarId(calendar: Calendar): List<ChatMessage> {
        return chatMessageRepository.findByCalendar(calendar)
    }
}
