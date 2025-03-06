package com.ll.TeamProject.global.userContext

import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.service.UserService
import com.ll.TeamProject.global.security.SecurityUser
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope


@RequestScope
@Component
class UserContext(
    private val resp: HttpServletResponse,
    private val req: HttpServletRequest,
    private val userService: UserService
) {
    // 요청을 보낸 사용자의 인증 정보를 가져와 실제 DB에 저장된 user 찾기
    fun findActor(): SiteUser? {
        val actor = getActor() ?: return null

        return userService.findById(actor.id!!).get()
    }

    // 요청을 보낸 사용자의 인증 정보를 가져와 해당 사용자를 조회
    fun getActor(): SiteUser? {
        return (SecurityContextHolder.getContext().authentication?.principal as? SecurityUser)
            ?.let { SiteUser(it.id, it.username) }
    }

    // 쿠키 생성
    fun setCookie(name: String, value: String) {
        val cookie = ResponseCookie.from(name, value)
            .path("/")
            .domain("localhost")
            .httpOnly(true)
            .build()

        resp.addHeader("Set-Cookie", cookie.toString())
    }

    // 장기 쿠키 생성
    fun setLongCookie(name: String, value: String) {
        val cookie = ResponseCookie.from(name, value)
            .path("/")
            .domain("localhost")
            .httpOnly(true)
            .maxAge(31536000)
            .build()

        resp.addHeader("Set-Cookie", cookie.toString())
    }

    // 요청에서 헤더 얻어오기
    fun getHeader(name: String): String? = req.getHeader(name)

    // 쿠키 값 얻기
    fun getCookieValue(name: String): String? {
        return req.cookies?.firstOrNull { it.name == name }?.value
    }

    // 응답 헤더 설정
    fun setHeader(name: String, value: String) {
        resp.setHeader(name, value)
    }

    // 로그인 설정
    fun setLogin(user: SiteUser) {
        val userId = user.id ?: throw IllegalStateException("로그인 사용자 ID가 없습니다.") // ✅ 로그인 실패 유도

        val userDetails = SecurityUser(
            userId,
            user.username,
            "",
            user.nickname,
            user.getAuthorities()
        )

        val authentication: Authentication = UsernamePasswordAuthenticationToken(
            userDetails,
            userDetails.password,
            userDetails.authorities
        )

        SecurityContextHolder.getContext().authentication = authentication
    }

    // 쿠키 삭제
    fun deleteCookie(name: String) {
        val cookie = ResponseCookie.from(name, "")
            .path("/")
            .domain("localhost")
            .httpOnly(true)
            .maxAge(0)
            .build()

        resp.addHeader("Set-Cookie", cookie.toString())
    }

    // JWT 생성하고 쿠키 생성
    fun makeAuthCookies(user: SiteUser): String {
        val accessToken = userService.genAccessToken(user)

        setCookie("apiKey", user.apiKey)
        setCookie("accessToken", accessToken)

        return accessToken
    }
}
