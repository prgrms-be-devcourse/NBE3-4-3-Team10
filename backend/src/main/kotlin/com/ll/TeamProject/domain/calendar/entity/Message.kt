package com.ll.TeamProject.domain.calendar.entity

import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.global.jpa.entity.BaseTime
import jakarta.persistence.*
import lombok.Getter

@Entity
@Getter
class Message(
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "user_id") // 작성자
    var user: SiteUser,

    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "calendar_id")
    var calendar: Calendar,

    var content: String, // 메시지 내용

    var isRead: Boolean // 읽음 여부
) : BaseTime() {
    // BaseTime은 id (BaseEntity, no setter), 생성/수정일 포함
}