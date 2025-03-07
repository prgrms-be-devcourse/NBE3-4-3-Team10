package com.ll.TeamProject.domain.chat.chat.entity

import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.global.jpa.entity.BaseTime
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
data class ChatRoom(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    val calendar: Calendar

) : BaseTime()
