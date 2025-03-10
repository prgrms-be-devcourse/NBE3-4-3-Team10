package com.ll.TeamProject.domain.chat.websocket

import com.ll.TeamProject.domain.chat.websocket.service.WsTokenService
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.socket.CloseStatus

class AuthenticatedChatHandler(
    private val wsTokenService: WsTokenService
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val wsToken = extractWsToken(session)
        println(session)

        if (wsToken == null || !wsTokenService.isValidWsToken(wsToken)) {
            println("❌ [WebSocket 인증 실패] 유효하지 않은 wsToken")
            session.close(CloseStatus.NOT_ACCEPTABLE)
            return
        }

        val user = wsTokenService.getUserFromWsToken(wsToken)

        if (user == null) {
            println("❌ [WebSocket 인증 실패] wsToken으로 유저 조회 실패")
            session.close(CloseStatus.NOT_ACCEPTABLE)
            return
        }

        session.attributes["user"] = user  // 인증된 사용자 정보 저장
        println("✅ [WebSocket 인증 성공] username=${user.username}")
    }

    private fun extractWsToken(session: WebSocketSession): String? {
        return session.handshakeHeaders.get("cookie")?.query
            ?.split("&")
            ?.associate { param ->
                val (key, value) = param.split("=", limit = 2)
                key to value
            }?.get("wsToken")
    }
}
