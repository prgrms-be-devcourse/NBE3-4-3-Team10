package com.ll.TeamProject.domain.schedule.repository

import com.ll.TeamProject.domain.schedule.entity.Schedule
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.*

interface ScheduleRepository : JpaRepository<Schedule, Long> {
    fun findOverlappingSchedules(
        calendarId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Schedule>

    fun findSchedulesByCalendarAndDateRange(
        calendarId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Schedule>

    fun findTopByOrderByIdDesc(): Optional<Schedule>
}
