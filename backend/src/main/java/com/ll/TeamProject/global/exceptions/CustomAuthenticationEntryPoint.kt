package com.ll.TeamProject.global.exceptions

import com.fasterxml.jackson.databind.ObjectMapper
import com.ll.TeamProject.global.globalExceptionHandler.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    private val objectMapper = ObjectMapper()

    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
        response.apply {
            contentType = "application/json;charset=UTF-8"
            status = HttpStatus.UNAUTHORIZED.value()
            writer.write(objectMapper.writeValueAsString(ErrorResponse("AUTH_005", "사용자 인증정보가 올바르지 않습니다.")))
        }
    }
}
