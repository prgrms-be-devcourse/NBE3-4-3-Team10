package com.ll.TeamProject.domain.calendar.dto

data class CalendarCreateDto(
    val userId: Long,
    val name: String,
    val description: String
)