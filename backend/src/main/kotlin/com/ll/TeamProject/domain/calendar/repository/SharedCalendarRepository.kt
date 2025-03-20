package com.ll.TeamProject.domain.calendar.repository

import com.ll.TeamProject.domain.calendar.entity.SharedCalendar
import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.user.entity.SiteUser
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SharedCalendarRepository : JpaRepository<SharedCalendar, Long> {
    @Query("SELECT sc.calendar FROM SharedCalendar sc WHERE sc.user.id = :userId")
    fun findSharedCalendarsByUserId(@Param("userId") userId: Long): List<Calendar>

    fun findByUserAndCalendar(user: SiteUser, calendar: Calendar): Optional<SharedCalendar>

    @Query("SELECT sc FROM SharedCalendar sc JOIN FETCH sc.calendar WHERE sc.sharedBy.id = :ownerId")
    fun findByOwnerId(@Param("ownerId") ownerId: Long): List<SharedCalendar>
}

