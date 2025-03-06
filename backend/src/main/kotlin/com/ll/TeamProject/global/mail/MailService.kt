package com.ll.TeamProject.global.mail

interface MailService {
    fun sendMail(to: String, subject: String, text: String)
    fun sendVerificationCode(nickname: String, email: String, verificationCode: String)
}
