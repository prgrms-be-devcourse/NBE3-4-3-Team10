package com.ll.TeamProject.domain.calendar.controller;

import com.ll.TeamProject.domain.calendar.dto.CalendarCreateDto;
import com.ll.TeamProject.domain.calendar.dto.CalendarUpdateDto;
import com.ll.TeamProject.domain.calendar.entity.Calendar;
import com.ll.TeamProject.domain.calendar.service.CalendarService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendars")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@SecurityRequirement(name = "bearerAuth")
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping
    public ResponseEntity<Calendar> createCalendar(@RequestBody CalendarCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(calendarService.createCalendar(dto));
    }

    @GetMapping
    public ResponseEntity<List<Calendar>> getAllCalendars() {
        return ResponseEntity.ok(calendarService.getAllCalendars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Calendar> getCalendarById(@PathVariable Long id) {
        return ResponseEntity.ok(calendarService.getCalendarById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Calendar> updateCalendar(@PathVariable Long id, @RequestBody CalendarUpdateDto dto) {
        return ResponseEntity.ok(calendarService.updateCalendar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCalendar(@PathVariable Long id) {
        calendarService.deleteCalendar(id);
        return ResponseEntity.ok("캘린더가 삭제되었습니다!");
    }
}
