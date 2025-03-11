package com.ll.TeamProject.domain.chat.websocket

import com.ll.TeamProject.domain.chat.websocket.service.WsTokenService
import com.ll.TeamProject.domain.user.entity.SiteUser
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage

class AuthenticatedChatHandler(
    private val wsTokenService: WsTokenService
) : TextWebSocketHandler() {

    private var nextHandler: ChatHandler? = null

    fun setNextHandler(handler: ChatHandler) {
        this.nextHandler = handler
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val wsToken = extractWsToken(session)
        println("🕵️‍♂️ [WebSocket 인증] 받은 wsToken: $wsToken")

        if (wsToken == null || !wsTokenService.isValidWsToken(wsToken)) {
            println("❌ [WebSocket 인증 실패] wsToken이 없습니다.")
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
        nextHandler?.afterConnectionEstablished(session)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        nextHandler?.handleTextMessage(session, message)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        nextHandler?.afterConnectionClosed(session, status)
        wsTokenService.removeWsToken(extractWsToken(session) ?: "")
    }

    private fun extractWsToken(session: WebSocketSession): String? {
        val uriQuery = session.uri?.query ?: return null
        return uriQuery.split("&")
            .map { it.split("=") }
            .firstOrNull { it.first() == "wsToken" }
            ?.getOrNull(1)
    }
}
