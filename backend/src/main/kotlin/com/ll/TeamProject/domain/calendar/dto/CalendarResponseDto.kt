package com.ll.TeamProject.domain.calendar.dto

import com.ll.TeamProject.domain.calendar.entity.Calendar

data class CalendarResponseDto(
    val id: Long,
    val name: String,
    val description: String
) {
    companion object {
        fun from(calendar: Calendar): CalendarResponseDto {
            return CalendarResponseDto(
                id = calendar.id ?: 0L, // nullable 방지
                name = calendar.name,
                description = calendar.description
            )
        }
    }
}