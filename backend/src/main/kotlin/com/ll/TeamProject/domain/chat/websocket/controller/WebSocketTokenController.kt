package com.ll.TeamProject.domain.chat.websocket.controller

import com.ll.TeamProject.domain.chat.websocket.service.WsTokenService
import com.ll.TeamProject.domain.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ws-token")
class WebSocketTokenController(
    private val userService: UserService,
    private val wsTokenService: WsTokenService
) {

    @PostMapping
    fun generateWsToken(request: HttpServletRequest): ResponseEntity<Map<String, String>> {
        val cookies = request.getHeader("Cookie")

        println("🕵️‍♂️ [DEBUG] Raw Cookie Header: $cookies")

        val accessToken = cookies?.split("; ")
            ?.find { it.startsWith("accessToken=") }
            ?.substringAfter("=")

        if (accessToken == null) {
            return ResponseEntity.status(401).body(mapOf("error" to "UNAUTHORIZED"))
        }

        val user = userService.getUserFromAccessToken(accessToken)
            ?: return ResponseEntity.status(401).body(mapOf("error" to "INVALID_TOKEN"))

        val wsToken = wsTokenService.createWsTokenForUser(user)

        println("✅ [DEBUG] Generated wsToken: $wsToken")

        return ResponseEntity.ok(mapOf("wsToken" to wsToken))
    }
}
