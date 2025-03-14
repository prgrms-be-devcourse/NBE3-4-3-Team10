package com.ll.TeamProject.domain.calendar.controller

import com.ll.TeamProject.domain.calendar.dto.CalendarCreateDto
import com.ll.TeamProject.domain.calendar.dto.CalendarResponseDto
import com.ll.TeamProject.domain.calendar.dto.CalendarUpdateDto
import com.ll.TeamProject.domain.calendar.service.CalendarService
import com.ll.TeamProject.domain.user.entity.SiteUser
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
    fun createCalendar(
        @AuthenticationPrincipal user: SiteUser,
        @RequestBody dto: CalendarCreateDto
    ): ResponseEntity<CalendarResponseDto> {
        return ResponseEntity.ok(calendarService.createCalendar(user, dto))
    }

    /**
     * ✅ 사용자의 모든 캘린더 조회
     */
    @GetMapping
    fun getAllCalendars(@AuthenticationPrincipal user: SiteUser): ResponseEntity<List<CalendarResponseDto>> {
        return ResponseEntity.ok(calendarService.getAllCalendars(user.id!!))
    }

    /**
     * ✅ 특정 캘린더 조회
     */
    @GetMapping("/{id}")
    fun getCalendarById(
        @AuthenticationPrincipal user: SiteUser,
        @PathVariable id: Long
    ): ResponseEntity<CalendarResponseDto> {
        return ResponseEntity.ok(calendarService.getCalendarByIdAsDto(id))
    }

    /**
     * ✅ 캘린더 수정
     */
    @PutMapping("/{id}")
    fun updateCalendar(
        @AuthenticationPrincipal user: SiteUser,
        @PathVariable id: Long,
        @RequestBody dto: CalendarUpdateDto
    ): ResponseEntity<CalendarResponseDto> {
        return ResponseEntity.ok(calendarService.updateCalendar(user, id, dto))
    }

    /**
     * ✅ 캘린더 삭제
     */
    @DeleteMapping("/{id}")
    fun deleteCalendar(
        @AuthenticationPrincipal user: SiteUser,
        @PathVariable id: Long
    ): ResponseEntity<Void> {  // ✅ 204 No Content 반환
        calendarService.deleteCalendar(user, id)
        return ResponseEntity.noContent().build()
    }

    /**
     * ✅ 사용자가 공유받은 캘린더 목록 조회
     */
    @GetMapping("/shared")
    fun getSharedCalendars(@AuthenticationPrincipal user: SiteUser): ResponseEntity<List<CalendarResponseDto>> {
        return ResponseEntity.ok(calendarService.getSharedCalendars(user.id!!))
    }

    /**
     * ✅ 특정 친구에게 캘린더 공유
     */
    @PostMapping("/{calendarId}/share/{friendId}")
    fun shareCalendar(
        @AuthenticationPrincipal user: SiteUser,
        @PathVariable calendarId: Long,
        @PathVariable friendId: Long
    ): ResponseEntity<String> {
        calendarService.shareCalendar(user, friendId, calendarId)
        return ResponseEntity.ok("캘린더 공유 성공!")
    }

    /**
     * ✅ 특정 친구와의 캘린더 공유 해제
     */
    @DeleteMapping("/{calendarId}/unshare/{friendId}")
    fun unshareCalendar(
        @AuthenticationPrincipal user: SiteUser,
        @PathVariable calendarId: Long,
        @PathVariable friendId: Long
    ): ResponseEntity<String> {
        calendarService.unshareCalendar(friendId, calendarId)
        return ResponseEntity.ok("캘린더 공유 해제 성공!")
    }
}
