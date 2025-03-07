package com.ll.TeamProject.domain.chat.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.ll.TeamProject.domain.calendar.repository.CalendarRepository
import com.ll.TeamProject.domain.chat.chat.dto.ChatMessageDto
import com.ll.TeamProject.domain.chat.chat.service.ChatService
import com.ll.TeamProject.domain.user.entity.SiteUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatHandler(
    private val chatSessionManager: ChatSessionManager,
    private val chatService: ChatService,
    private val objectMapper: ObjectMapper,
    private val calendarRepository: CalendarRepository
) : TextWebSocketHandler() {
//세션은어떻게가져올려했나요?
    private val log = LoggerFactory.getLogger(ChatHandler::class.java)

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val calendarId = extractCalendarId(session)
            ?: throw IllegalArgumentException("캘린더 ID를 URL에서 추출할 수 없습니다.")

        val user = session.attributes["user"] as? SiteUser
            ?: throw IllegalArgumentException("유효한 사용자 인증 정보를 확인할 수 없습니다.")

        chatSessionManager.addSession(calendarId, session, user)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val chatMessageDto = objectMapper.readValue(message.payload, ChatMessageDto::class.java)

        val sender = chatSessionManager.getUser(session)
            ?: throw IllegalStateException("세션에 사용자 정보가 없습니다. 정상적으로 인증되지 않은 WebSocket 세션입니다.")

        val calendar = calendarRepository.findById(chatMessageDto.calendarId)
            .orElseThrow { IllegalArgumentException("해당 캘린더가 존재하지 않습니다: ${chatMessageDto.calendarId}") }

        chatService.saveMessage(sender, calendar, chatMessageDto)

        chatSessionManager.broadcastMessage(chatMessageDto.calendarId, message)

        log.info("📩 [메시지 전송 완료] calendarId=${chatMessageDto.calendarId}, sender=${sender.username}, message=${chatMessageDto.message}")
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: org.springframework.web.socket.CloseStatus) {
        val calendarId = extractCalendarId(session) ?: return
        chatSessionManager.removeSession(calendarId, session)

        log.info("🔌 [WebSocket 연결 종료] calendarId=$calendarId, session=${session.id}")
    }

    private fun extractCalendarId(session: WebSocketSession): Long? {
        val uri = session.uri?.toString()
        val regex = """/api/calendars/(\d+)/chat""".toRegex()
        return regex.find(uri ?: return null)?.groupValues?.get(1)?.toLongOrNull()
    }
}
