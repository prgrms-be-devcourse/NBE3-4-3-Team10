package com.ll.TeamProject.domain.schedule.repository;

import com.ll.TeamProject.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // NamedQuery "Schedule.findOverlappingSchedules"를 사용
    List<Schedule> findOverlappingSchedules(@Param("calendarId") Long calendarId,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    // NamedQuery "Schedule.findSchedulesByCalendarAndDateRange"를 사용
    List<Schedule> findSchedulesByCalendarAndDateRange(@Param("calendarId") Long calendarId,
                                                       @Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);

    Optional<Schedule> findTopByOrderByIdDesc();
}
