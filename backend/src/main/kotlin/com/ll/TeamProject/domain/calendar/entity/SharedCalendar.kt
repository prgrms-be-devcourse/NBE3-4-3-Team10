package com.ll.TeamProject.domain.calendar.entity

import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.global.jpa.entity.BaseEntity
import jakarta.persistence.*
import lombok.Getter

@Entity
@Getter
class SharedCalendar(
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "calendar_id", nullable = false)
    var calendar: Calendar, // 캘린더 ID

    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "user_id", nullable = false)
    var user: SiteUser // 공유받은 사용자 ID
) : BaseEntity() {
    // BaseEntity는 id를 포함하고 있습니다.
}