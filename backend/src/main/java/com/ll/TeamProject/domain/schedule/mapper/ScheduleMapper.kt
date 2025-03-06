package com.ll.TeamProject.domain.schedule.mapper

import com.ll.TeamProject.domain.schedule.dto.ScheduleResponseDto
import com.ll.TeamProject.domain.schedule.entity.Schedule
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named

@Mapper(componentModel = "spring", uses = [ScheduleMapperHelper::class])
interface ScheduleMapper {

    @Mapping(target = "calendarId", source = "calendar.id")
    @Mapping(target = "modifyDate", expression = "java(ScheduleMapperHelper.defaultModifyDate(schedule.getModifyDate(), schedule))")
    fun toDto(schedule: Schedule): ScheduleResponseDto
}



