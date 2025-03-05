package com.ll.TeamProject.domain.schedule.dto;

import com.ll.TeamProject.global.jpa.entity.Location;
import org.springframework.data.jpa.domain.AbstractPersistable_.id

import java.time.LocalDateTime;

data class ScheduleResponseDto(
        val id:Long,
        val calendarId:Long,
        val title: String,
        val description: String="",
        val startTime: LocalDateTime,
        val endTime: LocalDateTime,
        val location: Location,
        val createDate: LocalDateTime,
        val modifyDate: LocalDateTime=createDate,
)
