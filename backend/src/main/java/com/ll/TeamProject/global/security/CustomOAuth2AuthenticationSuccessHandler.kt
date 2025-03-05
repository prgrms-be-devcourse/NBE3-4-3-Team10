package com.ll.TeamProject.global.security

import com.ll.TeamProject.global.userContext.UserContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomOAuth2AuthenticationSuccessHandler(
    private val userContext: UserContext,

    @Value("\${custom.dev.frontUrl}")
    private val devFrontUrl: String
) : SavedRequestAwareAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val user = userContext.findActor().orElseThrow()

        userContext.makeAuthCookies(user)

        response.sendRedirect("$devFrontUrl/calendars/")
    }
}
