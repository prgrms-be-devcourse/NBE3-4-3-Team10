package com.ll.TeamProject.global.security

import com.ll.TeamProject.domain.user.service.AuthenticationService
import com.ll.TeamProject.domain.user.service.JoinService
import com.ll.TeamProject.global.userContext.UserContext
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CustomOAuth2UserService(
    private val authenticationService: AuthenticationService,
    private val userContext: UserContext,
    private val joinService: JoinService
) : DefaultOAuth2UserService() {

    // 소셜 로그인이 성공할 때마다 이 함수가 실행된다.
    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)

        val oauthId = oAuth2User.name

        val providerTypeCode = userRequest
            .clientRegistration
            .registrationId
            .uppercase(Locale.getDefault())

        val attributes = oAuth2User.attributes

        val username = providerTypeCode + "__" + oauthId // KAKAO__12983719287
        val email: String

        if (providerTypeCode == "GOOGLE") {
            email = attributes["email"] as String
        } else {
            // providerTypeCode == "KAKAO"
            val accountProperties = attributes["kakao_account"] as Map<*, *>
            email = accountProperties["email"] as String
        }

        val user = joinService.findOrRegisterUser(username, email, providerTypeCode)

        authenticationService.modifyLastLogin(user)
        userContext.setLongCookie("lastLogin", providerTypeCode)

        return SecurityUser(
            user.id!!,
            user.username,
            "",
            user.nickname,
            user.getAuthorities()
        )
    }
}

