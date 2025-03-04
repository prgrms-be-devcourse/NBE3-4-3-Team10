package com.ll.TeamProject.domain.schedule.mapper;

import com.ll.TeamProject.domain.calendar.entity.Calendar;
import com.ll.TeamProject.domain.schedule.dto.ScheduleResponseDto;
import com.ll.TeamProject.domain.schedule.entity.Schedule;
import com.ll.TeamProject.global.jpa.entity.Location;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-04T14:34:38+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.2 (GraalVM Community)"
)
@Component
public class ScheduleMapperImpl implements ScheduleMapper {

    @Override
    public ScheduleResponseDto toDto(Schedule schedule) {
        if ( schedule == null ) {
            return null;
        }

        Long calendarId = null;
        Long id = null;
        String title = null;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        Location location = null;
        LocalDateTime createDate = null;

        calendarId = scheduleCalendarId( schedule );
        id = schedule.getId();
        title = schedule.getTitle();
        startTime = schedule.getStartTime();
        endTime = schedule.getEndTime();
        location = schedule.getLocation();
        createDate = schedule.getCreateDate();

        String description = schedule.getDescription() != null ? schedule.getDescription() : "";
        LocalDateTime modifyDate = schedule.getModifyDate() != null ? schedule.getModifyDate() : schedule.getCreateDate();

        ScheduleResponseDto scheduleResponseDto = new ScheduleResponseDto( id, calendarId, title, description, startTime, endTime, location, createDate, modifyDate );

        return scheduleResponseDto;
    }

    private Long scheduleCalendarId(Schedule schedule) {
        Calendar calendar = schedule.getCalendar();
        if ( calendar == null ) {
            return null;
        }
        return calendar.getId();
    }
}
