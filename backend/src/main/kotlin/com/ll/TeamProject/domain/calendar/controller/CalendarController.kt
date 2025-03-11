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
    /**
     * ✅ 캘린더 생성
     */
    @PostMapping
    fun createCalendar(@RequestBody dto: CalendarCreateDto): ResponseEntity<CalendarResponseDto> {
        val calendar = calendarService.createCalendar(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(CalendarResponseDto.from(calendar))
    }

    /**
     * ✅ 사용자의 모든 캘린더 조회
     */
    @GetMapping
    fun getAllCalendars(): ResponseEntity<List<CalendarResponseDto>> {
        val calendars = calendarService.getAllCalendars()
        val response = calendars.map { CalendarResponseDto.from(it) }
        return ResponseEntity.ok(response)
    }

    /**
     * ✅ 특정 캘린더 조회
     */
    @GetMapping("/{id}")
    fun getCalendarById(@PathVariable id: Long): ResponseEntity<CalendarResponseDto> {
        val calendar = calendarService.getCalendarById(id)
        return ResponseEntity.ok(CalendarResponseDto.from(calendar))
    }

    /**
     * ✅ 캘린더 수정
     */
    @PutMapping("/{id}")
    fun updateCalendar(@PathVariable id: Long, @RequestBody dto: CalendarUpdateDto): ResponseEntity<CalendarResponseDto> {
        val updatedCalendar = calendarService.updateCalendar(id, dto)
        return ResponseEntity.ok(CalendarResponseDto.from(updatedCalendar))
    }

    /**
     * ✅ 캘린더 삭제
     */
    @DeleteMapping("/{id}")
    fun deleteCalendar(@PathVariable id: Long): ResponseEntity<String> {
        calendarService.deleteCalendar(id)
        return ResponseEntity.ok("캘린더가 삭제되었습니다!")
    }

    /**
     * ✅ 사용자가 공유받은 캘린더 목록 조회
     */
    @GetMapping("/shared/{userId}")
    fun getSharedCalendars(@PathVariable userId: Long): ResponseEntity<List<CalendarResponseDto>> {
        val sharedCalendars = calendarService.getSharedCalendars(userId)
        val response = sharedCalendars.map { CalendarResponseDto.from(it) }
        return ResponseEntity.ok(response)
    }

    /**
     * ✅ 특정 친구에게 캘린더 공유
     */
    @PostMapping("/{calendarId}/share/{friendId}")
    fun shareCalendar(@PathVariable calendarId: Long, @PathVariable friendId: Long): ResponseEntity<String> {
        calendarService.shareCalendar(friendId, calendarId)
        return ResponseEntity.ok("캘린더 공유 성공!")
    }

    /**
     * ✅ 특정 친구와의 캘린더 공유 해제
     */
    @DeleteMapping("/{calendarId}/unshare/{friendId}")
    fun unshareCalendar(@PathVariable calendarId: Long, @PathVariable friendId: Long): ResponseEntity<String> {
        calendarService.unshareCalendar(friendId, calendarId)
        return ResponseEntity.ok("캘린더 공유 해제 성공!")
    }
}
