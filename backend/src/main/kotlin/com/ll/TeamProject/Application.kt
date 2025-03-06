package com.ll.TeamProject

import com.ll.TeamProject.global.mail.MailProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(MailProperties::class)
@EnableScheduling
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
