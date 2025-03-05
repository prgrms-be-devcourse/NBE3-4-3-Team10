package com.ll.TeamProject.domain.schedule.mapper;

import com.ll.TeamProject.domain.schedule.dto.ScheduleResponseDto;
import com.ll.TeamProject.domain.schedule.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    @Mapping(target = "calendarId", source = "calendar.id")
    @Mapping(target = "description", expression = "java(schedule.getDescription() != null ? schedule.getDescription() : \"\")")
    @Mapping(target = "modifyDate", expression = "java(schedule.getModifyDate() != null ? schedule.getModifyDate() : schedule.getCreateDate())")
    ScheduleResponseDto toDto(Schedule schedule);
}
