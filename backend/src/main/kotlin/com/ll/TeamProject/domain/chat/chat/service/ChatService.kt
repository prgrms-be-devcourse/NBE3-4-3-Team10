package com.ll.TeamProject.domain.chat.chat.service

import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.chat.chat.entity.ChatMessage
import com.ll.TeamProject.domain.chat.chat.entity.ChatRoom
import com.ll.TeamProject.domain.chat.chat.repository.ChatMessageRepository
import com.ll.TeamProject.domain.chat.chat.repository.ChatRoomRepository
import com.ll.TeamProject.domain.user.entity.SiteUser
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import org.springframework.transaction.annotation.Transactional


@Service
class ChatService(private val chatMessageRepository: ChatMessageRepository,
                  private val chatRoomRepository: ChatRoomRepository
) {

    @Transactional
    fun saveMessage(
        sender: SiteUser,
        calendar: Calendar,
        messageContent: String,
        sentAt: LocalDateTime
    ): ChatMessage {

        val chatRoom = chatRoomRepository.findByCalendar(calendar)
            ?: chatRoomRepository.save(ChatRoom(calendar = calendar))

        val chatMessage = ChatMessage(
            sender = sender,
            calendar = calendar,
            chatRoom = chatRoom,
            message = messageContent,
            sentAt = sentAt
        )

        return chatMessageRepository.save(chatMessage)
    }

    fun getMessagesByCalendarId(calendar: Calendar): List<ChatMessage> {
        return chatMessageRepository.findByCalendar(calendar)
    }
}


