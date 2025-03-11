package com.ll.TeamProject.domain.chat.chat.controller

import com.ll.TeamProject.domain.calendar.repository.CalendarRepository
import com.ll.TeamProject.domain.chat.chat.dto.ChatMessageDto
import com.ll.TeamProject.domain.chat.chat.service.ChatService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/calendars/{calendarId}/messages")
class ChatMessageController(
    private val calendarRepository: CalendarRepository,
    private val chatService: ChatService
) {

    @GetMapping
    fun getMessages(@PathVariable calendarId: Long): ResponseEntity<List<ChatMessageDto>> {
        val calendar = calendarRepository.findById(calendarId)
            .orElseThrow { IllegalArgumentException("캘린더 없음") }

        val messages = chatService.getMessagesByCalendarId(calendar)
            .map {
                ChatMessageDto(
                    senderId = it.sender.id!!,
                    calendarId = it.calendar.id!!,
                    message = it.message,
                    sentAt = it.sentAt
                )
            }
        return ResponseEntity.ok(messages)
    }
}