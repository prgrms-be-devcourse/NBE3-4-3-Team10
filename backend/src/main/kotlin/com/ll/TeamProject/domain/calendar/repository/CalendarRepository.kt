package com.ll.TeamProject.domain.calendar.repository

import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.user.entity.SiteUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CalendarRepository : JpaRepository<Calendar, Long> {

    @Query("SELECT c FROM Calendar c WHERE c.user.id = :userId")
    fun findCalendarsByUserId(@Param("userId") userId: Long): List<Calendar>

    @Query("SELECT c.user.username FROM Calendar c WHERE c.id = :calendarId")
    fun findUsernameByCalendarId(@Param("calendarId") calendarId: Long): Optional<String>

    fun findByName(name: String): Optional<Calendar>

    @Query("SELECT c FROM Calendar c JOIN FETCH c.sharedUsers sc WHERE sc.user = :user")
    fun findSharedCalendarsByUser(@Param("user") user: SiteUser): List<Calendar>
}

