package com.ll.TeamProject.domain.calendar.dto

import com.ll.TeamProject.domain.calendar.entity.Calendar

data class CalendarResponseDto(
    val id: Long,
    val name: String,
    val description: String,
    val sharedWith: String? = null // ✅ 공유한 사용자 정보 추가
) {
    companion object {
        fun from(calendar: Calendar, sharedWith: String? = null): CalendarResponseDto {
            return CalendarResponseDto(
                id = calendar.id!!,
                name = calendar.name,
                description = calendar.description,
                sharedWith = sharedWith
            )
        }
    }
}

