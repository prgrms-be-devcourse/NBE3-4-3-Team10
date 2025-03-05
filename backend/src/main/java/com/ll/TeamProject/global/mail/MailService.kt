package com.ll.TeamProject.global.mail;

public interface MailService {
    void sendMail(String to, String subject, String text);
    void sendVerificationCode(String nickname, String email, String verificationCode);
}
