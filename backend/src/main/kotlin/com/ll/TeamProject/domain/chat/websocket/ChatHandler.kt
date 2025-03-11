package com.ll.TeamProject.domain.chat.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.ll.TeamProject.domain.calendar.repository.CalendarRepository
import com.ll.TeamProject.domain.chat.chat.dto.ChatMessageDto
import com.ll.TeamProject.domain.chat.chat.service.ChatService
import com.ll.TeamProject.domain.user.entity.SiteUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
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

    private val log = LoggerFactory.getLogger(ChatHandler::class.java)

    public override fun afterConnectionEstablished(session: WebSocketSession) {
        val calendarId = extractCalendarId(session)
        if (calendarId == null) {
            log.error("❌ [WebSocket 연결 실패] 캘린더 ID를 URL에서 추출할 수 없습니다.")
            session.close()
            return
        }

        val user = session.attributes["user"] as? SiteUser
        if (user == null) {
            log.error("❌ [WebSocket 연결 실패] 유효한 사용자 인증 정보를 확인할 수 없습니다.")
            session.close()
            return
        }

        chatSessionManager.addSession(calendarId, session, user)
        log.info("✅ [WebSocket 연결 성공] calendarId=$calendarId, username=${user.username}")
    }

    public override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        log.info("📬 받은 메시지 payload=${message.payload}")

        try {
            val chatMessageDto = objectMapper.readValue(message.payload, ChatMessageDto::class.java)
            val sender = chatSessionManager.getUser(session)
                ?: throw IllegalStateException("세션에 사용자가 없습니다.")

            val calendar = calendarRepository.findById(chatMessageDto.calendarId)
                .orElseThrow { IllegalArgumentException("해당 캘린더 없음: ${chatMessageDto.calendarId}") }

            val savedMessage = chatService.saveMessage(
                sender = sender,
                calendar = calendar,
                messageContent = chatMessageDto.message,
                sentAt = chatMessageDto.sentAt
            )

            val responseDto = ChatMessageDto(
                senderId = sender.id!!,
                calendarId = calendar.id!!,
                message = savedMessage.message,
                sentAt = savedMessage.sentAt
            )

            val responseText = objectMapper.writeValueAsString(responseDto)
            chatSessionManager.broadcastMessage(calendar.id!!, TextMessage(responseText))

            log.info("📩 [메시지 전송 완료] calendarId=${calendar.id}, sender=${sender.username}, message=${savedMessage.message}")

        } catch (e: Exception) {
            log.error("📛 [메시지 처리 오류] ${e.message}")
        }
    }

    public override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val calendarId = extractCalendarId(session)
        if (calendarId != null) {
            chatSessionManager.removeSession(calendarId, session)
            log.info("🔌 [WebSocket 연결 종료] calendarId=$calendarId, session=${session.id}")
        }
    }

    private fun extractCalendarId(session: WebSocketSession): Long? {
        val uri = session.uri?.toString()
        val regex = """/api/calendars/(\d+)/chat""".toRegex()
        return regex.find(uri ?: return null)?.groupValues?.get(1)?.toLongOrNull()
    }
}

