package com.ll.TeamProject.domain.schedule.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.global.jpa.entity.BaseTime
import com.ll.TeamProject.global.jpa.entity.Location
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@NamedQueries(
    NamedQuery(
        name = "Schedule.findOverlappingSchedules",
        query = "SELECT s FROM Schedule s WHERE s.calendar.id = :calendarId AND (s.startTime < :endTime AND s.endTime > :startTime)"
    ),
    NamedQuery(
        name = "Schedule.findSchedulesByCalendarAndDateRange",
        query = "SELECT s FROM Schedule s WHERE s.calendar.id = :calendarId AND (s.startTime < :endDate AND s.endTime > :startDate)"
    )
)
class Schedule(
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    var calendar: Calendar,

    @Column(length = 200)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: SiteUser,

    var startTime: LocalDateTime,

    var endTime: LocalDateTime,

    @Embedded
    var location: Location
) : BaseTime() {

    fun update(
        title: String,
        description: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        location: Location
    ) {
        this.title = title
        this.description = description
        this.startTime = startTime
        this.endTime = endTime
        this.location = location
    }

    // 명시적 생성자 패턴
    constructor(calendar: Calendar, user: SiteUser) : this(
        calendar = calendar,
        title = "Untitled Schedule",
        description = "",
        user = user,
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now().plusHours(1),
        location = Location()
    )

    // 기본 생성자
    constructor() : this(Calendar(), SiteUser())
}
