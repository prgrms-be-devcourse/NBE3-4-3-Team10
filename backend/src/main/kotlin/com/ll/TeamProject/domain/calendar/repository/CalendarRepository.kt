package com.ll.TeamProject.domain.calendar.repository

import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.user.entity.SiteUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CalendarRepository : JpaRepository<Calendar, Long> {

    // 사용자 ID로 모든 캘린더 조회
    fun findByUserId(userId: Long): List<Calendar>

    // 특정 캘린더 ID로 해당 캘린더의 사용자 이름 조회
    @Query("SELECT c.user.username FROM Calendar c WHERE c.id = :calendarId")
    fun findUsernameByCalendarId(@Param("calendarId") calendarId: Long): String

    // 이름으로 캘린더 조회
    fun findByName(name: String): Optional<Calendar>

    // 사용자가 공유받은 캘린더 목록 조회
    @Query("SELECT c FROM Calendar c JOIN c.sharedUsers sc WHERE sc.user = :user")
    fun findSharedCalendarsByUser(@Param("user") user: SiteUser): List<Calendar>
}
