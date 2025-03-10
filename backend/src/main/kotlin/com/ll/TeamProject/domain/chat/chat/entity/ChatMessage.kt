package com.ll.TeamProject.domain.chat.chat.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.global.jpa.entity.BaseTime
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class ChatMessage(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    val sender: SiteUser,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    val calendar: Calendar,


    @Column(length = 1000)
    val message: String,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val sentAt: LocalDateTime = LocalDateTime.now()
) : BaseTime()
