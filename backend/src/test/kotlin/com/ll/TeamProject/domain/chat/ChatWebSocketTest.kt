package com.ll.TeamProject.domain.chat

import com.fasterxml.jackson.databind.ObjectMapper
import com.ll.TeamProject.domain.calendar.repository.CalendarRepository
import com.ll.TeamProject.domain.chat.chat.dto.ChatMessageDto
import com.ll.TeamProject.domain.chat.JwtTestHelper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.lang.reflect.Type
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ChatWebSocketTest(
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val jwtTestHelper: JwtTestHelper,
    @Autowired val calendarRepository: CalendarRepository
) {

    @Test
    fun `WebSocket 연결 및 메시지 전송 테스트`() {
        val client = WebSocketStompClient(StandardWebSocketClient()).apply {
            messageConverter = MappingJackson2MessageConverter()
        }

        val calendarId = 1L

        // 1. 캘린더 ID로 username 조회
        val username = calendarRepository.findUsernameByCalendarId(calendarId)

        // 2. 해당 username으로 AccessToken 발급
        val accessToken = jwtTestHelper.generateAccessToken(username)

        // 3. StompHeaders에 Authorization 헤더 추가
        val headers = StompHeaders().apply {
            add("Authorization", "Bearer $accessToken")
        }

        val messageQueue = ArrayBlockingQueue<ChatMessageDto>(1)

        // 4. WebSocket 연결 시도
        val session = client.connectAsync(
            "ws://localhost:8080/api/calendars/$calendarId/chat",
            object : StompSessionHandlerAdapter() {
                override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
                    // 5. 구독 (받는 경로 설정)
                    session.subscribe("/topic/calendars/$calendarId/chat", object : StompFrameHandler {
                        override fun getPayloadType(headers: StompHeaders): Type = ChatMessageDto::class.java

                        override fun handleFrame(headers: StompHeaders, payload: Any?) {
                            messageQueue.offer(payload as ChatMessageDto)
                        }
                    })

                    // 6. 메시지 전송 (보내는 경로 설정)
                    val message = ChatMessageDto(
                        senderId = 1L,  // 실제 서비스에서는 서버가 채워주는 값이지만 테스트에서는 임의 지정
                        calendarId = calendarId,
                        message = "테스트 메시지"
                    )
                    session.send("/app/calendars/$calendarId/chat/send", message)
                }
            }
        ).get(3, TimeUnit.SECONDS)

        // 7. 수신 메시지 검증
        val receivedMessage = messageQueue.poll(5, TimeUnit.SECONDS)

        assertEquals("테스트 메시지", receivedMessage?.message)
    }
}
