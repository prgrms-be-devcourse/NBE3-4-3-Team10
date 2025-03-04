package com.ll.TeamProject.domain.schedule.entity;

import com.ll.TeamProject.domain.calendar.entity.Calendar;
import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.global.jpa.entity.BaseTime;
import com.ll.TeamProject.global.jpa.entity.Location;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(
                name = "Schedule.findOverlappingSchedules",
                query = "SELECT s FROM Schedule s WHERE s.calendar.id = :calendarId AND (s.startTime < :endTime AND s.endTime > :startTime)"
        ),
        @NamedQuery(
                name = "Schedule.findSchedulesByCalendarAndDateRange",
                query = "SELECT s FROM Schedule s WHERE s.calendar.id = :calendarId AND (s.startTime < :endDate AND s.endTime > :startDate)"
        )
})
public class Schedule extends BaseTime {
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SiteUser user;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Location location;

    public void update(String title, String description,
                       LocalDateTime startTime, LocalDateTime endTime, Location location) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }
}
