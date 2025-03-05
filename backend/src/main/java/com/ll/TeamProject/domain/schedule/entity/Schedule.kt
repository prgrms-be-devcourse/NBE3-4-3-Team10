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

    constructor() : this(
        calendar = Calendar(),
        title = "",
        description = "",
        user = SiteUser(),
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now(),
        location = Location()
    )
}
