package com.ll.TeamProject.domain.calendar.service

import com.ll.TeamProject.domain.calendar.dto.CalendarCreateDto
import com.ll.TeamProject.domain.calendar.dto.CalendarResponseDto
import com.ll.TeamProject.domain.calendar.dto.CalendarUpdateDto
import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.calendar.entity.SharedCalendar
import com.ll.TeamProject.domain.calendar.repository.CalendarRepository
import com.ll.TeamProject.domain.calendar.repository.SharedCalendarRepository
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.repository.UserRepository
import com.ll.TeamProject.global.exceptions.ServiceException
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
@Transactional
class CalendarService(
    private val calendarRepository: CalendarRepository,
    private val sharedCalendarRepository: SharedCalendarRepository,
    private val userRepository: UserRepository,
    private val calendarOwnerValidator: CalendarOwnerValidator
) {
    private val log: Logger = LoggerFactory.getLogger(CalendarService::class.java)

    companion object {
        private const val CALENDAR_NOT_FOUND = "캘린더를 찾을 수 없습니다."
    }

    /**
     * ✅ 캘린더 생성 (DTO 반환)
     */
    fun createCalendar(user: SiteUser, dto: CalendarCreateDto): CalendarResponseDto {
        val calendar = calendarRepository.save(Calendar(user, dto.name, dto.description))
        log.info("📌 캘린더 생성 완료 - ID: ${calendar.id}, Name: ${calendar.name}")
        return CalendarResponseDto.from(calendar)
    }

    /**
     * ✅ 사용자의 모든 캘린더 조회 (DTO 반환)
     */
    fun getAllCalendars(userId: Long): List<CalendarResponseDto> {
        return calendarRepository.findCalendarsByUserId(userId)
            .map { CalendarResponseDto.from(it) }
    }

    /**
     * ✅ 특정 캘린더 조회 (DTO 반환)
     */
    fun getCalendarByIdAsDto(id: Long): CalendarResponseDto {
        val calendar = getCalendarById(id)
        return CalendarResponseDto.from(calendar)
    }

    private fun getCalendarById(id: Long): Calendar {
        return calendarRepository.findById(id)
            .orElseThrow { ServiceException("404", CALENDAR_NOT_FOUND) }
    }

    /**
     * ✅ 캘린더 수정 (DTO 반환)
     */
    fun updateCalendar(user: SiteUser, id: Long, dto: CalendarUpdateDto): CalendarResponseDto {
        val calendar = getCalendarById(id)
        calendarOwnerValidator.validate(calendar, user)

        calendar.update(dto)
        log.info("📌 캘린더 수정 완료 - ID: $id, New Name: ${dto.name}, New Description: ${dto.description}")

        return CalendarResponseDto.from(calendarRepository.save(calendar))
    }

    /**
     * ✅ 캘린더 삭제
     */
    fun deleteCalendar(user: SiteUser, id: Long) {
        val calendar = getCalendarById(id)
        calendarOwnerValidator.validate(calendar, user)

        calendarRepository.deleteById(id)
        log.info("📌 캘린더 삭제 완료 - ID: $id")
    }

    /**
     * ✅ 사용자가 공유받은 캘린더 목록 조회 (DTO 반환)
     */
    fun getSharedCalendars(userId: Long): List<CalendarResponseDto> {
        return sharedCalendarRepository.findSharedCalendarsByUserId(userId)
            .map { CalendarResponseDto.from(it) }
    }

    /**
     * ✅ 특정 친구에게 캘린더 공유
     */
    fun shareCalendar(owner: SiteUser, friendId: Long, calendarId: Long) {
        val friend = userRepository.findById(friendId)
            .orElseThrow { ServiceException("404", "친구를 찾을 수 없습니다!") }

        val calendar = getCalendarById(calendarId)

        sharedCalendarRepository.findByUserAndCalendar(friend, calendar).ifPresent {
            throw ServiceException("400", "이미 공유된 캘린더입니다!")
        }

        sharedCalendarRepository.save(SharedCalendar(calendar, friend, owner))
        log.info("📌 캘린더 공유 완료 - Calendar ID: $calendarId, Shared with: ${friend.username}")
    }

    /**
     * ✅ 특정 친구와의 캘린더 공유 해제
     */
    fun unshareCalendar(friendId: Long, calendarId: Long) {
        val sharedCalendar = sharedCalendarRepository.findByUserAndCalendar(
            userRepository.findById(friendId).orElseThrow { ServiceException("404", "친구를 찾을 수 없습니다!") },
            getCalendarById(calendarId)
        ).orElseThrow { ServiceException("404", "공유된 캘린더 정보를 찾을 수 없습니다!") }

        sharedCalendarRepository.delete(sharedCalendar)
    }
}
