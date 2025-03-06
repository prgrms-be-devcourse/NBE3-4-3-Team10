package com.ll.TeamProject.domain.schedule.mapper

import com.ll.TeamProject.domain.schedule.entity.Schedule
import java.time.LocalDateTime

object ScheduleMapperHelper {
    @JvmStatic
    fun defaultModifyDate(modifyDate: LocalDateTime?, schedule: Schedule): LocalDateTime {
        return modifyDate ?: schedule.createDate
    }
}
