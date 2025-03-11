package com.ll.TeamProject.global.websocket

import com.ll.TeamProject.domain.chat.websocket.ChatHandler
import com.ll.TeamProject.domain.chat.websocket.AuthenticatedChatHandler
import com.ll.TeamProject.domain.chat.websocket.service.WsTokenService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val chatHandler: ChatHandler,
    private val wsTokenService: WsTokenService
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(authenticatedChatHandler(), "/api/calendars/{calendarId}/chat")
            .setAllowedOrigins("*")

        registry.addHandler(chatHandler, "/ws/chat") // 메시지 처리용 핸들러 추가
            .setAllowedOrigins("*")
    }

    @Bean
    fun authenticatedChatHandler() = AuthenticatedChatHandler(wsTokenService).apply {
        setNextHandler(chatHandler)
    }
}


