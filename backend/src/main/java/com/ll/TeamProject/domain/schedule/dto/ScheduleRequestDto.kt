package com.ll.TeamProject.domain.schedule.dto

import com.ll.TeamProject.global.jpa.entity.Location
import java.time.LocalDateTime

data class ScheduleRequestDto(
    val title: String,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val location: Location
) {
    init {
        require(title.length in 1..100) { "제목은 1~100자 이내여야 합니다." }
        require(description.length <= 500) { "설명은 최대 500자까지 입력할 수 있습니다." } // ✅ null 체크 제거
        require(!startTime.isBefore(LocalDateTime.now())) { "시작 시간은 현재 또는 미래여야 합니다." }
        require(!endTime.isBefore(LocalDateTime.now())) { "종료 시간은 현재 또는 미래여야 합니다." }
        require(startTime.isBefore(endTime)) { "종료 시간은 시작 시간보다 늦어야 합니다." }
    }
}

