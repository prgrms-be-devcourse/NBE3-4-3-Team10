package com.ll.TeamProject.domain.calendar.service

import com.ll.TeamProject.domain.calendar.dto.CalendarCreateDto
import com.ll.TeamProject.domain.calendar.dto.CalendarUpdateDto
import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.calendar.repository.CalendarRepository
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.global.exceptions.ServiceException
import com.ll.TeamProject.global.userContext.UserContextService
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
@Transactional
class CalendarService(
    private val calendarRepository: CalendarRepository,
    private val userContextService: UserContextService
) {

    private val log: Logger = LoggerFactory.getLogger(CalendarService::class.java)

    companion object {
        private const val CALENDAR_NOT_FOUND = "캘린더를 찾을 수 없습니다."
    }

    // 캘린더 생성
    fun createCalendar(dto: CalendarCreateDto): Calendar {
        val user: SiteUser = userContextService.getAuthenticatedUser()
        val calendar = Calendar(user, dto.name, dto.description)
        val savedCalendar = calendarRepository.save(calendar)

        log.info("캘린더 생성 완료 - ID: {}, Name: {}", savedCalendar.id, savedCalendar.name)
        return savedCalendar
    }

    // 사용자의 모든 캘린더 조회
    fun getAllCalendars(): List<Calendar> {
        val user: SiteUser = userContextService.getAuthenticatedUser()
        val userId = user.id ?: throw IllegalStateException("User ID cannot be null") // 예외 처리 추가
        return calendarRepository.findByUserId(userId)
    }

    // 특정 캘린더 조회 (소유자 검증 포함)
    fun getCalendarById(id: Long): Calendar {
        val user: SiteUser = userContextService.getAuthenticatedUser()
        val calendar = calendarRepository.findById(id)
            .orElseThrow { ServiceException("404", CALENDAR_NOT_FOUND) }

        checkCalendarOwnership(calendar, user)
        return calendar
    }

    // 캘린더 수정 (소유자 검증 포함)
    fun updateCalendar(id: Long, dto: CalendarUpdateDto): Calendar {
        val user: SiteUser = userContextService.getAuthenticatedUser()
        val calendar = getCalendarById(id)
        checkCalendarOwnership(calendar, user)

        calendar.update(dto.name, dto.description)
        log.info("캘린더 수정 완료 - ID: {}, New Name: {}, New Description: {}", id, dto.name, dto.description)

        return calendarRepository.save(calendar)
    }

    // 캘린더 삭제 (소유자 검증 포함)
    fun deleteCalendar(id: Long) {
        val user: SiteUser = userContextService.getAuthenticatedUser()
        val calendar = getCalendarById(id)
        checkCalendarOwnership(calendar, user)

        calendarRepository.deleteById(id)
        log.info("캘린더 삭제 완료 - ID: {}", id)
    }

    // 캘린더 소유자 검증
    private fun checkCalendarOwnership(calendar: Calendar, user: SiteUser) {
        if (calendar.user.id != user.id) {
            throw ServiceException("403", "캘린더 소유자만 접근할 수 있습니다.")
        }
    }
}