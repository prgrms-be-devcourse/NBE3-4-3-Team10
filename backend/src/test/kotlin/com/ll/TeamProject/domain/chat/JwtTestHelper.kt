package com.ll.TeamProject.domain.chat

import com.ll.TeamProject.domain.user.service.UserService
import org.springframework.stereotype.Component

@Component
class JwtTestHelper(
    private val userService: UserService
) {
    fun generateAccessToken(username: String): String {
        val user = userService.findByUsername(username).orElseThrow {
            IllegalArgumentException("테스트 유저($username)를 찾을 수 없습니다.")
        }
        return userService.genAccessToken(user)
    }
}
