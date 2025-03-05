package com.ll.TeamProject.domain.user.service;

import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.domain.user.enums.Role;
import com.ll.TeamProject.standard.util.Jwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthTokenService {

    @Value("${custom.jwt.secretKey}")
    private String secretKey;

    @Value("${custom.accessToken.expirationSeconds}")
    private long accessTokenExpirationSeconds;

    String genAccessToken(SiteUser user) {
        long id = user.getId();
        String username = user.getUsername();
        String role = user.getRole().name();
        String nickname = user.getNickname();

        return Jwt.toString(
                secretKey,
                accessTokenExpirationSeconds,
                Map.of("id", id, "username", username, "nickname", nickname, "role", role)
        );
    }

    public Map<String, Object> payload(String accessToken) {
        Map<String, Object> parsedPayload = Jwt.payload(secretKey, accessToken);

        if (parsedPayload == null) return null;

        long id = (long) (Integer) parsedPayload.get("id");
        String username = (String) parsedPayload.get("username");
        Role role = Role.valueOf((String) parsedPayload.get("role"));
        String nickname = (String) parsedPayload.get("nickname");

        return Map.of("id", id, "username", username, "role", role, "nickname", nickname);
    }
}
