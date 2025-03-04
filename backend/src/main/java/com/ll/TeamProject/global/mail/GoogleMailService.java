package com.ll.TeamProject.global.mail;

import com.ll.TeamProject.domain.user.exceptions.UserErrorCode;
import com.ll.TeamProject.global.exceptions.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleMailService implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage(); // 간단한 텍스트 메일
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new CustomException(UserErrorCode.EMAIL_SEND_FAILURE);
        }
    }

    @Override
    public void sendVerificationCode(String nickname, String email, String verificationCode) {
        String content = String.format("안녕하세요, %s님.\n\n인증번호: %s\n인증번호는 3분 동안 유효합니다.", nickname, verificationCode);
        sendMail(email, "계정 인증번호", content);
    }
}
