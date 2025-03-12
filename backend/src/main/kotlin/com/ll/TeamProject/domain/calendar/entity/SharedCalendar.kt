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
    var calendar: Calendar, // ✅ 공유된 캘린더

    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "user_id", nullable = false)
    var user: SiteUser, // ✅ 공유받는 사용자

    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "owner_id", nullable = false) // ✅ 추가: 공유한 사용자(캘린더 소유자)
    var owner: SiteUser // ✅ 공유한 사용자 (캘린더 소유자)
) : BaseEntity()
