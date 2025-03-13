package com.ll.TeamProject.domain.calendar.repository

import com.ll.TeamProject.domain.calendar.entity.SharedCalendar
import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.user.entity.SiteUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SharedCalendarRepository : JpaRepository<SharedCalendar, Long> {
    fun findByUserId(userId: Long): List<SharedCalendar>
    fun findByUserAndCalendar(user: SiteUser, calendar: Calendar): SharedCalendar?
}
