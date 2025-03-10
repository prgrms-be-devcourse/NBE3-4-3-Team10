package com.ll.TeamProject.domain.chat.websocket

import com.ll.TeamProject.domain.chat.websocket.service.WsTokenService
import com.ll.TeamProject.domain.user.entity.SiteUser
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.socket.CloseStatus

class AuthenticatedChatHandler(
    private val wsTokenService: WsTokenService
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val wsToken = extractWsToken(session)
        println("🕵️‍♂️ [WebSocket 인증] 받은 wsToken: $wsToken")

        if (wsToken == null) {
            println("❌ [WebSocket 인증 실패] wsToken이 없습니다.")
            session.close(CloseStatus.NOT_ACCEPTABLE)
            return
        }

        if (!wsTokenService.isValidWsToken(wsToken)) {
            println("❌ [WebSocket 인증 실패] 유효하지 않은 wsToken: $wsToken")
            session.close(CloseStatus.NOT_ACCEPTABLE)
            return
        }

        val user = wsTokenService.getUserFromWsToken(wsToken)
        if (user == null) {
            println("❌ [WebSocket 인증 실패] wsToken으로 유저 조회 실패: $wsToken")
            session.close(CloseStatus.NOT_ACCEPTABLE)
            return
        }

        session.attributes["user"] = user
        println("✅ [WebSocket 인증 성공] username=${user.username}, sessionId=${session.id}")
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val wsToken = extractWsToken(session)
        if (wsToken != null) {
            wsTokenService.removeWsToken(wsToken)
        }
        println("🔌 [WebSocket 연결 종료] sessionId=${session.id}, reason=${status.reason}")
    }

    /**
     * WebSocket 세션에서 wsToken을 추출 (URI 파라미터 또는 쿠키)
     */
    private fun extractWsToken(session: WebSocketSession): String? {
        val uriQuery = session.uri?.query
        if (!uriQuery.isNullOrEmpty()) {
            val queryParams = uriQuery.split("&").associate {
                val (key, value) = it.split("=")
                key to value
            }
            return queryParams["wsToken"]
        }

        val cookies = session.handshakeHeaders["cookie"] ?: return null
        return cookies.flatMap { it.split(";") }
            .map { it.trim().split("=", limit = 2) }
            .firstOrNull { it.first() == "wsToken" }
            ?.getOrNull(1)
    }
}
