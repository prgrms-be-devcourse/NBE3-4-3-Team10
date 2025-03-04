package com.ll.TeamProject.domain.user.service;

import com.ll.TeamProject.domain.user.entity.Authentication;
import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.domain.user.repository.AuthenticationRepository;
import com.ll.TeamProject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationRepository authenticationRepository;
    private final UserRepository userRepository;

    public void modifyLastLogin(SiteUser user) {
        authenticationRepository.findByUserId(user.getId())
                .ifPresent(authentication -> {
                    authentication.setLastLogin();
                    authentication.resetFailedAttempts();
                    authenticationRepository.save(authentication);
                });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleLoginFailure(SiteUser user) {
        authenticationRepository.findByUserId(user.getId()).ifPresent(authentication -> {

            int failedLogin = authentication.failedLogin();

            if (failedLogin >= 5) {
                user.lockAccount();
                userRepository.save(user);
            }

            authenticationRepository.save(authentication);
        });
    }

    public Optional<Authentication> findByUserId(Long id) {
        return authenticationRepository.findByUserId(id);
    }
}
