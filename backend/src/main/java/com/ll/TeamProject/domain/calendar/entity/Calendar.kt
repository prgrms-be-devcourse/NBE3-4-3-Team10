package com.ll.TeamProject.domain.calendar.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.ll.TeamProject.domain.calendar.dto.CalendarUpdateDto
import com.ll.TeamProject.domain.schedule.entity.Schedule
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.global.jpa.entity.BaseTime
import jakarta.persistence.*
import lombok.AccessLevel
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
class Calendar(
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "user_id", nullable = false)
    var user: SiteUser, // 사용자
    var name: String, // 캘린더 이름
    var description: String // 캘린더 설명
) : BaseTime() {

    // 공유된 사용자 목록
    @OneToMany(mappedBy = "calendar", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    val sharedUsers: MutableList<SharedCalendar> = mutableListOf()

    // 메시지 목록
    @OneToMany(mappedBy = "calendar", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    val messageList: MutableList<Message> = mutableListOf()

    // 일정 목록
    @OneToMany(mappedBy = "calendar", cascade = [CascadeType.REMOVE])
    @JsonIgnore
    val schedules: MutableList<Schedule> = mutableListOf()

    // 캘린더 정보 업데이트 (record 적용)
    fun update(updateDto: CalendarUpdateDto) {
        this.name = updateDto.name
        this.description = updateDto.description
    }

    // 캘린더 이름 및 설명 변경
    fun update(name: String, description: String) {
        this.name = name
        this.description = description
    }
}