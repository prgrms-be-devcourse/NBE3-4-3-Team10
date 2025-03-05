package com.ll.TeamProject.domain.user.service;

import com.ll.TeamProject.domain.user.entity.Authentication;
import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.domain.user.enums.AuthType;
import com.ll.TeamProject.domain.user.repository.AuthenticationRepository;
import com.ll.TeamProject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.ll.TeamProject.domain.user.enums.Role.USER;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final AuthenticationRepository authenticationRepository;

    public SiteUser findOrRegisterUser(String username, String email, String providerTypeCode) {
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(email))
                .orElseGet(() -> join(username, "", email, providerTypeCode));
    }

    public SiteUser join(String username, String password, String email, String providerTypeCode) {
        SiteUser user = SiteUser.builder()
                .username(username)
                .password(password)
                .nickname(username)
                .email(email)
                .role(USER)
                .apiKey(UUID.randomUUID().toString())
                .locked(false)
                .build();
        user = userRepository.save(user);

        AuthType authType = AuthType.valueOf(providerTypeCode);
        Authentication authentication = Authentication
                .builder()
                .user(user)
                .authType(authType)
                .failedAttempts(0)
                .build();
        authenticationRepository.save(authentication);

        return user;
    }

}
