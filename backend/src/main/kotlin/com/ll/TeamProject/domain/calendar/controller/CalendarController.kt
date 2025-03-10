package com.ll.TeamProject.domain.calendar.controller

import com.ll.TeamProject.domain.calendar.dto.CalendarCreateDto
import com.ll.TeamProject.domain.calendar.dto.CalendarResponseDto
import com.ll.TeamProject.domain.calendar.dto.CalendarUpdateDto
import com.ll.TeamProject.domain.calendar.service.CalendarService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/calendars")
@CrossOrigin(origins = ["http://localhost:3000"], allowCredentials = "true")
@SecurityRequirement(name = "bearerAuth")
class CalendarController(
    private val calendarService: CalendarService
) {
    // 캘린더 생성 엔드포인트
    @PostMapping
    fun createCalendar(@RequestBody dto: CalendarCreateDto): ResponseEntity<CalendarResponseDto> {
        val calendar = calendarService.createCalendar(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(CalendarResponseDto.from(calendar))
    }
    // 모든 캘린더 조회 엔드포인트
    @GetMapping
    fun getAllCalendars(): ResponseEntity<List<CalendarResponseDto>> {
        val calendars = calendarService.getAllCalendars()
        val response = calendars.map { CalendarResponseDto.from(it) }
        return ResponseEntity.ok(response)
    }
    // 특정 캘린더 조회 엔드포인트
    @GetMapping("/{id}")
    fun getCalendarById(@PathVariable id: Long): ResponseEntity<CalendarResponseDto> {
        val calendar = calendarService.getCalendarById(id)
        return if (calendar != null) {
            ResponseEntity.ok(CalendarResponseDto.from(calendar))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }
    // 캘린더 수정 엔드포인트
    @PutMapping("/{id}")
    fun updateCalendar(@PathVariable id: Long, @RequestBody dto: CalendarUpdateDto): ResponseEntity<CalendarResponseDto> {
        val updatedCalendar = calendarService.updateCalendar(id, dto)
        return ResponseEntity.ok(CalendarResponseDto.from(updatedCalendar))
    }
    // 캘린더 삭제 엔드포인트
    @DeleteMapping("/{id}")
    fun deleteCalendar(@PathVariable id: Long): ResponseEntity<String> {
        calendarService.deleteCalendar(id)
        return ResponseEntity.ok("캘린더가 삭제되었습니다!")
    }
}
