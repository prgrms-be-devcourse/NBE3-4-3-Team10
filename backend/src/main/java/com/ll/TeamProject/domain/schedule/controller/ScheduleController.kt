package com.ll.TeamProject.domain.schedule.controller

import com.ll.TeamProject.domain.schedule.dto.ScheduleRequestDto
import com.ll.TeamProject.domain.schedule.dto.ScheduleResponseDto
import com.ll.TeamProject.domain.schedule.service.ScheduleService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate


@RestController
@Tag(name = "ScheduleController", description = "스케줄 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/calendars/{calendarId}/schedules")
class ScheduleController(
    private val scheduleService: ScheduleService
) {

    @Operation(summary = "일정 생성", description = "주어진 calendarId에 새 일정을 생성합니다.")
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "일정 생성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            ApiResponse(responseCode = "500", description = "서버 오류")
    )
    @PostMapping
    fun createSchedule(
            @PathVariable calendarId:Long,
            @RequestBody @Valid scheduleRequestDto:ScheduleRequestDto
    ): ResponseEntity<ScheduleResponseDto> {
        val responseDto = scheduleService.createSchedule(calendarId, scheduleRequestDto);
        return ResponseEntity.ok(responseDto)
    }

    @Operation(summary = "일정 수정", description = "기존 일정(scheduleId)을 수정합니다.")
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "일정 수정 성공"),
            ApiResponse(responseCode = "404", description = "일정이 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "권한이 없음"),
            ApiResponse(responseCode = "500", description = "서버 오류")
    )
    @PutMapping("/{scheduleId}")
    fun updateSchedule(
            @PathVariable calendarId:Long,
            @PathVariable scheduleId:Long,
            @RequestBody @Valid scheduleRequestDto:ScheduleRequestDto
    ): ResponseEntity<ScheduleResponseDto> {
        val responseDto = scheduleService.updateSchedule(calendarId, scheduleId, scheduleRequestDto);
        return ResponseEntity.ok(responseDto)
    }

    @Operation(summary = "일정 삭제", description = "지정된 일정(scheduleId)을 삭제합니다.")
    @ApiResponses(
            ApiResponse(responseCode = "204", description = "일정 삭제 성공"),
            ApiResponse(responseCode = "404", description = "일정이 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "권한이 없음"),
            ApiResponse(responseCode = "500", description = "서버 오류")
    )
    @DeleteMapping("/{scheduleId}")
    fun deleteSchedule(
            @PathVariable calendarId:Long,
            @PathVariable scheduleId:Long
    ): ResponseEntity<Void> {
        scheduleService.deleteSchedule(calendarId, scheduleId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "일정 목록 조회", description = "지정된 날짜(date)에 해당하는 일정을 조회합니다.")
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "일정 목록 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 날짜 형식"),
            ApiResponse(responseCode = "403", description = "권한이 없음"),
            ApiResponse(responseCode = "500", description = "서버 오류")
    )
    @GetMapping
    fun getSchedules(
        @PathVariable calendarId:Long,
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<List<ScheduleResponseDto>> {
        val schedules = scheduleService.getSchedules(calendarId, startDate, endDate)
        return ResponseEntity.ok(schedules)
    }

    @Operation(summary = "하루 일정 조회", description = "특정 날짜(24시간)의 일정을 조회합니다.")
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "하루 일정 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 날짜 형식"),
            ApiResponse(responseCode = "403", description = "권한이 없음"),
            ApiResponse(responseCode = "500", description = "서버 오류")
    )
    @GetMapping("/daily")
    fun getDailySchedules(
            @PathVariable calendarId: Long,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<List<ScheduleResponseDto>> {
        val schedules = scheduleService.getDailySchedules(calendarId, date)
        return ResponseEntity.ok(schedules)
    }

    @Operation(summary = "주별 일정 조회", description = "지정된 날짜가 속한 주의 일정을 조회합니다.")
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "주별 일정 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 날짜 형식"),
            ApiResponse(responseCode = "403", description = "권한이 없음"),
            ApiResponse(responseCode = "500", description = "서버 오류")
    )
    @GetMapping("/weekly")
    fun getWeeklySchedules(
            @PathVariable calendarId:Long,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<List<ScheduleResponseDto>> {
        val schedules = scheduleService.getWeeklySchedules(calendarId, date)
        return ResponseEntity.ok(schedules)
    }

    @Operation(summary = "월별 일정 조회", description = "지정된 날짜가 속한 월의 일정을 조회합니다.")
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "월별 일정 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 날짜 형식"),
            ApiResponse(responseCode = "403", description = "권한이 없음"),
            ApiResponse(responseCode = "500", description = "서버 오류")
    )
    @GetMapping("/monthly")
    fun getMonthlySchedules(
            @PathVariable calendarId:Long,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<List<ScheduleResponseDto>> {
        val schedules = scheduleService.getMonthlySchedules(calendarId, date);
        return ResponseEntity.ok(schedules);
    }


    @Operation(summary = "특정 일정 조회", description = "주어진 일정 ID(scheduleId)를 기반으로 특정 일정을 조회합니다.")
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "일정 조회 성공"),
            ApiResponse(responseCode = "404", description = "일정이 존재하지 않음"),
            ApiResponse(responseCode = "403", description = "권한이 없음"),
            ApiResponse(responseCode = "500", description = "서버 오류")
    )
    @GetMapping("/{scheduleId}")
    fun getSchedule(
            @PathVariable calendarId: Long,
            @PathVariable scheduleId:Long
    ): ResponseEntity<ScheduleResponseDto> {
        val responseDto = scheduleService.getScheduleById(calendarId, scheduleId);
        return ResponseEntity.ok(responseDto);
    }
}
