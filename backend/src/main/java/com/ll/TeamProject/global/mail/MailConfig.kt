package com.ll.TeamProject.global.mail

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class MailConfig(
    private val mailProperties: MailProperties
) {

    @Bean
    fun mailSender(): JavaMailSender {
        return JavaMailSenderImpl().apply {
            host = mailProperties.host
            port = mailProperties.port
            username = mailProperties.username
            password = mailProperties.password

            javaMailProperties.apply {
                putAll(mailProperties.properties)
            }
        }
    }
}
