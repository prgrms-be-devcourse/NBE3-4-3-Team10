package com.ll.TeamProject.domain.calendar.repository;

import com.ll.TeamProject.domain.calendar.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    List<Calendar> findByUserId(Long userId);

    @Query("SELECT c.user.username FROM Calendar c WHERE c.id = :calendarId")
    String findUsernameByCalendarId(@Param("calendarId") Long calendarId);

    Optional<Calendar> findByName(String name);
}