package com.ll.TeamProject.domain.calendar.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.ll.TeamProject.domain.calendar.dto.CalendarUpdateDto
import com.ll.TeamProject.domain.schedule.entity.Schedule
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.global.jpa.entity.BaseTime
import jakarta.persistence.*

@Entity
class Calendar(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: SiteUser, // 캘린더 소유자

    var name: String, // 캘린더 이름

    var description: String // 캘린더 설명
) : BaseTime() {

    // ✅ 공유된 사용자 목록 (캘린더 소유자 포함)
    @OneToMany(mappedBy = "calendar", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    val sharedUsers: MutableList<SharedCalendar> = mutableListOf()

    // 일정 목록
    @OneToMany(mappedBy = "calendar", cascade = [CascadeType.REMOVE])
    @JsonIgnore
    val schedules: MutableList<Schedule> = mutableListOf()

    // ✅ 캘린더 정보 업데이트
    // 캘린더 정보 업데이트
    fun update(updateDto: CalendarUpdateDto) {
        this.name = updateDto.name
        this.description = updateDto.description
    }

    // ✅ 특정 사용자에게 캘린더 공유 (소유자 정보 포함)
    fun addSharedUser(user: SiteUser, owner: SiteUser) {
        if (sharedUsers.none { it.user == user }) {
            sharedUsers.add(SharedCalendar(this, user, owner)) // ✅ owner 명시적으로 전달
        }
    }

    // ✅ 특정 사용자와의 공유 해제
    fun removeSharedUser(user: SiteUser) {
        sharedUsers.removeIf { it.user == user }
    }

    // 기본 생성자
    constructor() : this(SiteUser(), "Default Name", "")
}
