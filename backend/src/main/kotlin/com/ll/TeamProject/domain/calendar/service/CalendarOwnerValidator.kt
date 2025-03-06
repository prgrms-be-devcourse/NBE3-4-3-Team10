package com.ll.TeamProject.domain.calendar.service

import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.global.exceptions.ServiceException
import org.springframework.stereotype.Component

@Component
class CalendarOwnerValidator {

    fun validate(calendar: Calendar, user: SiteUser) {
        if (calendar.user.id != user.id) {
            throw ServiceException("403", "캘린더 소유자만 접근할 수 있습니다.")
        }
    }
}
