package com.ll.TeamProject.domain.user.service;

import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.domain.user.exceptions.UserErrorCode;
import com.ll.TeamProject.domain.user.repository.UserRepository;
import com.ll.TeamProject.global.exceptions.CustomException;
import com.ll.TeamProject.global.mail.GoogleMailService;
import com.ll.TeamProject.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AccountVerificationService {
    private static final String VERIFICATION_CODE_KEY = "verificationCode:";
    private static final String PASSWORD_RESET_KEY = "password-reset:";
    private static final int VERIFICATION_CODE_EXPIRATION = 180;
    private static final int PASSWORD_RESET_EXPIRATION = 300;

    private final UserRepository userRepository;
    private final GoogleMailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

    public void processVerification(String username, String email) {
        SiteUser user = validateUsernameAndEmail(username, email);

        String code = generateVerificationCode();
        String redisKey = getKey(VERIFICATION_CODE_KEY, username);

        redisService.setValue(redisKey, code, VERIFICATION_CODE_EXPIRATION);
        sendVerificationEmail(user.getNickname(), user.getEmail(), code);
    }

    private SiteUser validateUsernameAndEmail(String username, String email) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getEmail().equals(email))
                .orElseThrow(() -> new CustomException(UserErrorCode.INVALID_USERNAME_OR_EMAIL));
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1000000));
    }

    private void sendVerificationEmail(String nickname, String email, String verificationCode) {
        emailService.sendVerificationCode(nickname, email, verificationCode);
    }

    public void verifyAndUnlockAccount(String username, String verificationCode) {
        String redisKey = getKey(VERIFICATION_CODE_KEY, username);

        redisService.getValue(redisKey)
                .ifPresentOrElse(storedCode -> {

                    if (!verificationCode.equals(storedCode)) {
                        throw new CustomException(UserErrorCode.VERIFICATION_CODE_MISMATCH);
                    }

                    redisService.deleteValue(redisKey);
                    redisService.setValue(getKey(PASSWORD_RESET_KEY, username), username, PASSWORD_RESET_EXPIRATION);
                }, () -> {

                    throw new CustomException(UserErrorCode.VERIFICATION_CODE_EXPIRED);
                });
    }

    public void changePassword(String username, String password) {
        String redisKey = getKey(PASSWORD_RESET_KEY, username);
        String storedUsername = redisService.getValue(redisKey)
                .orElseThrow(() -> new CustomException(UserErrorCode.VERIFICATION_CODE_EXPIRED));

        if (!username.equals(storedUsername)) {
            redisService.deleteValue(redisKey);
            throw new CustomException(UserErrorCode.INVALID_REQUEST);
        }

        SiteUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        user.changePassword(passwordEncoder.encode(password));
        unlockAccount(user);
        redisService.deleteValue(redisKey);
    }

    private void unlockAccount(SiteUser user) {
        user.unlockAccount();
        userRepository.save(user);
    }

    private String getKey(String prefix, String username) {
        return prefix + username;
    }
}
