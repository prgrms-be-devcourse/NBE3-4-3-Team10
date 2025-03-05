package com.ll.TeamProject.domain.schedule.mapper

import com.ll.TeamProject.domain.schedule.dto.ScheduleResponseDto
import com.ll.TeamProject.domain.schedule.entity.Schedule
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import java.time.LocalDateTime

@Mapper(componentModel = "spring")
interface ScheduleMapper {

    @Mapping(target = "calendarId", source = "calendar.id")
    @Mapping(target = "description", source = "description", qualifiedByName = ["defaultDescription"])
    @Mapping(target = "modifyDate", source = "modifyDate", qualifiedByName = ["defaultModifyDate"])
    fun toDto(schedule: Schedule): ScheduleResponseDto

    @Named("defaultDescription")
    fun defaultDescription(description: String?): String {
        return description ?: ""
    }

    @Named("defaultModifyDate")
    fun defaultModifyDate(modifyDate: LocalDateTime?, schedule: Schedule): LocalDateTime {
        return modifyDate ?: schedule.createDate ?: LocalDateTime.now()
    }

}
