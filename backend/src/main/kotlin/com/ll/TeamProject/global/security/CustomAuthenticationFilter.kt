package com.ll.TeamProject.global.security

import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.service.UserService
import com.ll.TeamProject.global.userContext.UserContext
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class CustomAuthenticationFilter(
    private val userContext: UserContext,
    private val userService: UserService
) : OncePerRequestFilter() {

    internal data class AuthTokens(
        val apiKey: String,
        val accessToken: String
    )

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestURI = request.requestURI
        if (!requestURI.startsWith("/api") || requestURI in listOf("/api/admin/login", "/api/admin/logout")) {
            filterChain.doFilter(request, response)
            return
        }

        val authTokens = getAuthTokensFromRequest()

        if (authTokens == null) {
            filterChain.doFilter(request, response)
            return
        }

        val apiKey = authTokens.apiKey
        val accessToken = authTokens.accessToken

        var user = userService.getUserFromAccessToken(accessToken)

        if (user == null) user = refreshAccessTokenByApiKey(apiKey)

        if (user != null) userContext.setLogin(user)

        filterChain.doFilter(request, response)
    }

    private fun refreshAccessTokenByApiKey(apiKey: String): SiteUser? {
        return userService.findByApiKey(apiKey).orElse(null)?.also { user ->
            refreshAccessToken(user)
        }
    }

    private fun refreshAccessToken(user: SiteUser) {
        val newAccessToken = userService.genAccessToken(user)

        userContext.setHeader("Authorization", "Bearer " + user.apiKey + " " + newAccessToken)
        userContext.setCookie("accessToken", newAccessToken)
    }

    private fun getAuthTokensFromRequest(): AuthTokens? {
        userContext.getHeader("Authorization")
            ?.takeIf { it.startsWith("Bearer ") }
            ?.substring("Bearer ".length)
            ?.split(" ", limit = 2)
            ?.takeIf { it.size == 2 }
            ?.let { return AuthTokens(it[0], it[1]) }

        val apiKey = userContext.getCookieValue("apiKey")
        val accessToken = userContext.getCookieValue("accessToken")

        return if (apiKey != null && accessToken != null) AuthTokens(apiKey, accessToken) else null
    }

}
