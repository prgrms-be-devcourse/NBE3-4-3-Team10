package com.ll.TeamProject.domain.calendar.entity

import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.global.jpa.entity.BaseTime
import jakarta.persistence.*

@Entity
class Notification(
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "user_id", nullable = false)
    var user: SiteUser, // 알림 대상 사용자 (FK)

    var content: String, // 알림 내용

    var isRead: Boolean = false // 읽음 여부 (기본값 false)
) : BaseTime() {
    // BaseTime은 id, createdDate, updatedDate 등을 포함
}