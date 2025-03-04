package com.ll.TeamProject.domain.schedule.dto;

import com.ll.TeamProject.global.jpa.entity.Location;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ScheduleRequestDto(
        @NotNull @Size(min = 1, max = 100, message = "제목은 1~100자 이내여야 합니다.")
        String title,

        @Size(max = 500, message = "설명은 최대 500자까지 입력할 수 있습니다.")
        String description,

        @NotNull @FutureOrPresent(message = "시작 시간은 현재 또는 미래여야 합니다.")
        LocalDateTime startTime,

        @NotNull @FutureOrPresent(message = "종료 시간은 현재 또는 미래여야 합니다.")
        LocalDateTime endTime,

        Location location
) {
    @AssertTrue(message = "종료 시간은 시작 시간보다 늦어야 합니다.")
    public boolean isEndTimeValid() {
        return startTime.isBefore(endTime);
    }
}
