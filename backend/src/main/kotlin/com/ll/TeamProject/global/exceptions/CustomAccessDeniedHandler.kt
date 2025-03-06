package com.ll.TeamProject.global.exceptions

import com.fasterxml.jackson.databind.ObjectMapper
import com.ll.TeamProject.global.globalExceptionHandler.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {

    private val log: Logger = LoggerFactory.getLogger(CustomAccessDeniedHandler::class.java)
    private val objectMapper = ObjectMapper()

    override fun handle(request: HttpServletRequest, response: HttpServletResponse, accessDeniedException: AccessDeniedException) {
        log.warn("🔒 접근 권한 없음 요청 URL: {}", request.requestURI)

        response.apply {
            contentType = "application/json;charset=UTF-8"
            status = HttpStatus.FORBIDDEN.value()

            setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate")
            setHeader("Pragma", "no-cache")
            setHeader("Expires", "0")

            writer.write(objectMapper.writeValueAsString(ErrorResponse("USER_005", "접근 권한이 없습니다.")))
        }
    }
}
