package com.ll.TeamProject.domain.schedule.dto;

import com.ll.TeamProject.global.jpa.entity.Location;

import java.time.LocalDateTime;

public record ScheduleResponseDto(
        Long id,
        Long calendarId,
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Location location,
        LocalDateTime createDate,
        LocalDateTime modifyDate
) {
    public ScheduleResponseDto(
            Long id, Long calendarId, String title, String description,
            LocalDateTime startTime, LocalDateTime endTime, Location location,
            LocalDateTime createDate, LocalDateTime modifyDate
    ) {
        this.id = id;
        this.calendarId = calendarId;
        this.title = title;
        this.description = (description != null) ? description : ""; // null이면 빈 문자열 처리
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.createDate = createDate;
        this.modifyDate = (modifyDate != null) ? modifyDate : createDate; // null이면 createDate와 동일하게 설정
    }
}
