package com.ll.TeamProject.global.mail

import com.ll.TeamProject.domain.user.exceptions.UserErrorCode
import com.ll.TeamProject.global.exceptions.CustomException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class GoogleMailService(
    private val mailSender: JavaMailSender
) : MailService {

    override fun sendMail(to: String, subject: String, text: String) {
        val message = SimpleMailMessage().apply {
            setTo(to)
            setSubject(subject)
            setText(text)
        }

        runCatching {
            mailSender.send(message)
        }.onFailure {
            throw CustomException(UserErrorCode.EMAIL_SEND_FAILURE)
        }
    }

    override fun sendVerificationCode(nickname: String, email: String, verificationCode: String) {
        val content = """
            안녕하세요, $nickname 님.
            
            인증번호: $verificationCode
            인증번호는 3분 동안 유효합니다.
        """.trimIndent()

        sendMail(email, "계정 인증번호", content)
    }
}
