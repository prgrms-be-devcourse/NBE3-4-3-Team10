package com.ll.TeamProject.global.mail

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.mail")
data class MailProperties(

    var host: String = "",
    var port: Int = 0,
    var username: String = "",
    var password: String = "",
    var properties: MutableMap<String, String> = mutableMapOf()
)
