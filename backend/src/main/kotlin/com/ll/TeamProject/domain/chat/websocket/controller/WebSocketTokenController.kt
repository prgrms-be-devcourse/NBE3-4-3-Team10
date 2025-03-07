package com.ll.TeamProject.domain.chat.websocket.controller

import com.ll.TeamProject.domain.chat.websocket.service.WsTokenService
import com.ll.TeamProject.domain.user.service.UserService
import com.ll.TeamProject.global.userContext.UserContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ws-token")
class WebSocketTokenController(
    private val userService: UserService,
    private val wsTokenService: WsTokenService,
    private val userContext: UserContext
) {

    @PostMapping
    fun generateWsToken(): ResponseEntity<Map<String, String>> {
        // 1. 쿠키에서 accessToken 가져오기
        val accessToken = userContext.getCookieValue("accessToken")
            ?: return ResponseEntity.status(401).body(mapOf("error" to "UNAUTHORIZED"))

        // 2. accessToken 검증 및 사용자 조회
        val user = userService.getUserFromAccessToken(accessToken)
            ?: return ResponseEntity.status(401).body(mapOf("error" to "INVALID_TOKEN"))

        // 3. wsToken 발급
        val wsToken = wsTokenService.createWsTokenForUser(user)

        // 4. 클라이언트에 wsToken 반환
        return ResponseEntity.ok(mapOf("wsToken" to wsToken))
    }
}
