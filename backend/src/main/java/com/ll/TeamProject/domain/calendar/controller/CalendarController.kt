package com.ll.TeamProject.domain.calendar.controller

import com.ll.TeamProject.domain.calendar.dto.CalendarCreateDto
import com.ll.TeamProject.domain.calendar.dto.CalendarUpdateDto
import com.ll.TeamProject.domain.calendar.entity.Calendar
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

    @PostMapping
    fun createCalendar(@RequestBody dto: CalendarCreateDto): ResponseEntity<Calendar> {
        return ResponseEntity.status(HttpStatus.CREATED).body(calendarService.createCalendar(dto))
    }

    @GetMapping
    fun getAllCalendars(): ResponseEntity<List<Calendar>> {
        return ResponseEntity.ok(calendarService.getAllCalendars())
    }

    @GetMapping("/{id}")
    fun getCalendarById(@PathVariable id: Long): ResponseEntity<Calendar> {
        return ResponseEntity.ok(calendarService.getCalendarById(id))
    }

    @PutMapping("/{id}")
    fun updateCalendar(@PathVariable id: Long, @RequestBody dto: CalendarUpdateDto): ResponseEntity<Calendar> {
        return ResponseEntity.ok(calendarService.updateCalendar(id, dto))
    }

    @DeleteMapping("/{id}")
    fun deleteCalendar(@PathVariable id: Long): ResponseEntity<String> {
        calendarService.deleteCalendar(id)
        return ResponseEntity.ok("캘린더가 삭제되었습니다!")
    }
}