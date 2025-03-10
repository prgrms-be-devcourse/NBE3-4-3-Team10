package com.ll.TeamProject.global.websocket

import com.ll.TeamProject.domain.chat.websocket.ChatHandler
import com.ll.TeamProject.domain.chat.websocket.AuthenticatedChatHandler
import com.ll.TeamProject.domain.chat.websocket.service.WsTokenService
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val chatHandler: ChatHandler,  // 실제 메시지 처리 핸들러 (유지)
    private val wsTokenService: WsTokenService // 새로 추가 (wsToken 검증용 서비스)
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(AuthenticatedChatHandler(wsTokenService), "/api/calendars/{calendarId}/chat")
            .setAllowedOrigins("*")
    }
}
