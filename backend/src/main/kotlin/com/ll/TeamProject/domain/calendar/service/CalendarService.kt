package com.ll.TeamProject.domain.calendar.service

import com.ll.TeamProject.domain.calendar.dto.CalendarCreateDto
import com.ll.TeamProject.domain.calendar.dto.CalendarUpdateDto
import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.calendar.repository.CalendarRepository
import com.ll.TeamProject.global.exceptions.ServiceException
import com.ll.TeamProject.global.userContext.UserContext
import com.ll.TeamProject.global.userContext.UserContextService
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
@Transactional
class CalendarService(
    private val calendarRepository: CalendarRepository,
    private val userContextService: UserContextService,
    private val calendarOwnerValidator: CalendarOwnerValidator,
    private val userContext: UserContext
) {

    private val log: Logger = LoggerFactory.getLogger(CalendarService::class.java)

    companion object {
        private const val CALENDAR_NOT_FOUND = "캘린더를 찾을 수 없습니다."
    }

    // 캘린더 생성
    fun createCalendar(dto: CalendarCreateDto): Calendar {
        val user = userContext.findActor() ?: throw ServiceException("401", "인증된 사용자를 찾을 수 없습니다.")
        val calendar = Calendar(user, dto.name, dto.description)
        val savedCalendar = calendarRepository.save(calendar)

        log.info("캘린더 생성 완료 - ID: ${savedCalendar.id}, Name: ${savedCalendar.name}")
        return savedCalendar
    }

    // 사용자의 모든 캘린더 조회
    fun getAllCalendars(): List<Calendar> {
        val user = userContextService.getAuthenticatedUser()
        val userId = user.id ?: throw IllegalStateException("User ID cannot be null")
        return calendarRepository.findByUserId(userId)
    }

    // 특정 캘린더 조회 (소유자 검증 포함)
    fun getCalendarById(id: Long): Calendar {
        val user = userContextService.getAuthenticatedUser()
        val calendar = calendarRepository.findById(id)
            .orElseThrow { ServiceException("404", CALENDAR_NOT_FOUND) }

        calendarOwnerValidator.validate(calendar, user)
        return calendar
    }

    // 캘린더 수정 (소유자 검증 포함)
    fun updateCalendar(id: Long, dto: CalendarUpdateDto): Calendar {
        val user = userContextService.getAuthenticatedUser()
        val calendar = getCalendarById(id)
        calendarOwnerValidator.validate(calendar, user)

        calendar.update(dto.name, dto.description)
        log.info("캘린더 수정 완료 - ID: $id, New Name: ${dto.name}, New Description: ${dto.description}")

        return calendarRepository.save(calendar)
    }

    // 캘린더 삭제 (소유자 검증 포함)
    fun deleteCalendar(id: Long) {
        val user = userContextService.getAuthenticatedUser()
        val calendar = getCalendarById(id)
        calendarOwnerValidator.validate(calendar, user)

        calendarRepository.deleteById(id)
        log.info("캘린더 삭제 완료 - ID: $id")
    }
}
