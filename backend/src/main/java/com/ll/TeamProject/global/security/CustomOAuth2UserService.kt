package com.ll.TeamProject.global.security;

import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.domain.user.service.AuthenticationService;
import com.ll.TeamProject.domain.user.service.JoinService;
import com.ll.TeamProject.global.userContext.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AuthenticationService authenticationService;
    private final UserContext userContext;
    private final JoinService joinService;

    // 소셜 로그인이 성공할 때마다 이 함수가 실행된다.
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String oauthId = oAuth2User.getName();

        String providerTypeCode = userRequest
                .getClientRegistration()
                .getRegistrationId()
                .toUpperCase(Locale.getDefault());

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String username = providerTypeCode + "__" + oauthId; // KAKAO__12983719287
        String nickname = null;
        String email = null;

        if (providerTypeCode.equals("GOOGLE")) {

            email = (String) attributes.get("email");
            nickname = (String) attributes.get("name");

        } else if (providerTypeCode.equals("KAKAO")) {

            Map<String, String> attributesProperties = (Map<String, String>) attributes.get("properties");
            nickname = attributesProperties.get("nickname");

            Map<String, String> accountProperties = (Map<String, String>) attributes.get("kakao_account");
            email = accountProperties.get("email");
        }

        SiteUser user = joinService.findOrRegisterUser(username, email, providerTypeCode);

        authenticationService.modifyLastLogin(user);
        userContext.setLongCookie("lastLogin", providerTypeCode);

        return new SecurityUser(
                user.getId(),
                user.getUsername(),
                "",
                user.getNickname(),
                user.getAuthorities()
        );
    }
}

