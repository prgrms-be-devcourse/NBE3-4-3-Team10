package com.ll.TeamProject.domain.schedule.service

import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.calendar.repository.CalendarRepository
import com.ll.TeamProject.domain.schedule.dto.ScheduleRequestDto
import com.ll.TeamProject.domain.schedule.dto.ScheduleResponseDto
import com.ll.TeamProject.domain.schedule.entity.Schedule
import com.ll.TeamProject.domain.schedule.mapper.ScheduleMapper
import com.ll.TeamProject.domain.schedule.repository.ScheduleRepository
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.global.exceptions.ServiceException
import com.ll.TeamProject.global.userContext.UserContextService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
@Transactional
open class ScheduleService(
    private val scheduleRepository: ScheduleRepository,
    private val calendarRepository: CalendarRepository,
    private val scheduleMapper: ScheduleMapper,
    private val userContextService: UserContextService
) {
    // 일정 생성
    fun createSchedule(calendarId: Long, scheduleRequestDto: ScheduleRequestDto): ScheduleResponseDto {
        val user = userContextService.authenticatedUser
        val calendar = validateCalendarOwner(calendarId, user)

        val schedule = Schedule(
            calendar,
            scheduleRequestDto.title,
            scheduleRequestDto.description,
            user,
            scheduleRequestDto.startTime,
            scheduleRequestDto.endTime,
            scheduleRequestDto.location
        )
        return scheduleMapper.toDto(scheduleRepository.save(schedule))
    }

    // 일정 수정
    fun updateSchedule(
        calendarId: Long,
        scheduleId: Long,
        scheduleRequestDto: ScheduleRequestDto
    ): ScheduleResponseDto {
        val user = userContextService.authenticatedUser
        val schedule = validateScheduleOwnership(calendarId, scheduleId, user, "수정")

        schedule.update(
            scheduleRequestDto.title,
            scheduleRequestDto.description,
            scheduleRequestDto.startTime,
            scheduleRequestDto.endTime,
            scheduleRequestDto.location
        )
        return scheduleMapper.toDto(schedule)
    }

    // 일정 삭제
    fun deleteSchedule(calendarId: Long, scheduleId: Long) {
        val user = userContextService.authenticatedUser
        validateScheduleOwnership(calendarId, scheduleId, user, "삭제")

        scheduleRepository.deleteById(scheduleId)
    }

    // 일정 목록 조회
    fun getSchedules(calendarId: Long, startDate: LocalDate, endDate: LocalDate): List<ScheduleResponseDto> {
        val user = userContextService.authenticatedUser
        validateCalendarOwner(calendarId, user)

        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(LocalTime.MAX)

        return scheduleRepository.findSchedulesByCalendarAndDateRange(calendarId, startDateTime, endDateTime)
            .map { scheduleMapper.toDto(it) }
    }

    // 하루 일정 조회
    fun getDailySchedules(calendarId: Long, date: LocalDate): List<ScheduleResponseDto> {
        val user = userContextService.authenticatedUser
        validateCalendarOwner(calendarId, user)

        val (start, end) = getDayRange(date)

        return scheduleRepository.findSchedulesByCalendarAndDateRange(calendarId, start, end)
            .map { scheduleMapper.toDto(it) }
    }

    // 주별 일정 조회
    fun getWeeklySchedules(calendarId: Long, date: LocalDate): List<ScheduleResponseDto> {
        val user = userContextService.authenticatedUser
        validateCalendarOwner(calendarId, user)

        val (start, end) = getWeekRange(date)

        return scheduleRepository.findSchedulesByCalendarAndDateRange(calendarId, start, end)
            .map { scheduleMapper.toDto(it) }
    }


    // 월별 일정 조회
    fun getMonthlySchedules(calendarId: Long, date: LocalDate): List<ScheduleResponseDto> {
        val user = userContextService.authenticatedUser
        validateCalendarOwner(calendarId, user)

        val (start, end) = getMonthRange(date)

        return scheduleRepository.findSchedulesByCalendarAndDateRange(calendarId, start, end)
            .map { scheduleMapper.toDto(it) }
    }


    // 특정 일정 세부 조회
    fun getScheduleById(calendarId: Long, scheduleId: Long): ScheduleResponseDto {
        val user = userContextService.authenticatedUser
        validateCalendarOwner(calendarId, user)

        val schedule = getScheduleByIdOrThrow(scheduleId)
        validateScheduleBelongsToCalendar(schedule, calendarId)

        return scheduleMapper.toDto(schedule)
    }


    // --- 내부 헬퍼 메서드 ---
    // 캘린더 존재 및 소유자 검증
    private fun validateCalendarOwner(calendarId: Long, user: SiteUser): Calendar {
        val calendar = getCalendarByIdOrThrow(calendarId)
        checkCalendarOwnership(calendar, user)
        return calendar
    }

    // 일정 존재 확인
    private fun getScheduleByIdOrThrow(scheduleId: Long): Schedule {
        return scheduleRepository.findById(scheduleId)
            .orElseThrow { ServiceException("404", "해당 일정을 찾을 수 없습니다.") }!!
    }

    // 캘린더 존재 확인
    private fun getCalendarByIdOrThrow(calendarId: Long): Calendar {
        return calendarRepository.findById(calendarId)
            .orElseThrow { ServiceException("404", "해당 캘린더를 찾을 수 없습니다.") }
    }

    // 캘린더 소유자 검증
    private fun checkCalendarOwnership(calendar: Calendar, user: SiteUser) {
        if (calendar.user.id != user.id) {
            throw ServiceException("403", "캘린더 소유자만 접근할 수 있습니다.")
        }
    }

    // 일정이 해당 calendarId에 속하는지 검증하는 메서드
    private fun validateScheduleBelongsToCalendar(schedule: Schedule, calendarId: Long) {
        if (schedule.calendar.id != calendarId) {
            throw ServiceException("400", "해당 일정은 요청한 캘린더에 속하지 않습니다.")
        }
    }

    // 일정 수정/삭제 시 캘린더 및 일정 소유자 검증
    private fun validateScheduleOwnership(
        calendarId: Long,
        scheduleId: Long,
        user: SiteUser,
        action: String
    ): Schedule {
        validateCalendarOwner(calendarId, user)
        val schedule = getScheduleByIdOrThrow(scheduleId)
        validateScheduleBelongsToCalendar(schedule, calendarId)

        if (schedule.user.id != user.id) {
            throw ServiceException("403", "일정을 " + action + "할 권한이 없습니다.")
        }
        return schedule
    }


    // 하루 날짜 범위 계산
    private fun getDayRange(date: LocalDate): Array<LocalDateTime> {
        return arrayOf(date.atStartOfDay(), date.atTime(LocalTime.MAX))
    }

    // 주간 날짜 범위 계산
    private fun getWeekRange(date: LocalDate): Array<LocalDateTime> {
        val startOfWeek = date.with(DayOfWeek.SUNDAY)
        val endOfWeek = date.with(DayOfWeek.SATURDAY)
        return arrayOf(startOfWeek.atStartOfDay(), endOfWeek.atTime(LocalTime.MAX))
    }

    // 월간 날짜 범위 계산
    private fun getMonthRange(date: LocalDate): Array<LocalDateTime> {
        val firstDay = date.withDayOfMonth(1)
        val lastDay = date.withDayOfMonth(date.lengthOfMonth())
        return arrayOf(firstDay.atStartOfDay(), lastDay.atTime(LocalTime.MAX))
    }
}
