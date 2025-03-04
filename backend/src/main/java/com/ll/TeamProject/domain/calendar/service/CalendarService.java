package com.ll.TeamProject.domain.calendar.service;

import com.ll.TeamProject.domain.calendar.dto.CalendarCreateDto;
import com.ll.TeamProject.domain.calendar.dto.CalendarUpdateDto;
import com.ll.TeamProject.domain.calendar.entity.Calendar;
import com.ll.TeamProject.domain.calendar.repository.CalendarRepository;
import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.global.exceptions.ServiceException;
import com.ll.TeamProject.global.userContext.UserContextService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final UserContextService userContextService;

    private static final String CALENDAR_NOT_FOUND = "캘린더를 찾을 수 없습니다.";

    // 캘린더 생성
    public Calendar createCalendar(CalendarCreateDto dto) {
        SiteUser user = userContextService.getAuthenticatedUser();
        Calendar calendar = new Calendar(user, dto.name(), dto.description());
        Calendar savedCalendar = calendarRepository.save(calendar);

        log.info("캘린더 생성 완료 - ID: {}, Name: {}", savedCalendar.getId(), savedCalendar.getName());
        return savedCalendar;
    }

    // 사용자의 모든 캘린더 조회
    public List<Calendar> getAllCalendars() {
        SiteUser user = userContextService.getAuthenticatedUser();
        return calendarRepository.findByUserId(user.getId());
    }

    // 특정 캘린더 조회 (소유자 검증 포함)
    public Calendar getCalendarById(Long id) {
        SiteUser user = userContextService.getAuthenticatedUser();
        Calendar calendar = calendarRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404", CALENDAR_NOT_FOUND));

        checkCalendarOwnership(calendar, user);
        return calendar;
    }

    // 캘린더 수정 (소유자 검증 포함)
    public Calendar updateCalendar(Long id, CalendarUpdateDto dto) {
        SiteUser user = userContextService.getAuthenticatedUser();
        Calendar calendar = getCalendarById(id);
        checkCalendarOwnership(calendar, user);

        calendar.update(dto.name(), dto.description());
        log.info("캘린더 수정 완료 - ID: {}, New Name: {}, New Description: {}", id, dto.name(), dto.description());

        return calendarRepository.save(calendar);
    }

    // 캘린더 삭제 (소유자 검증 포함)
    public void deleteCalendar(Long id) {
        SiteUser user = userContextService.getAuthenticatedUser();
        Calendar calendar = getCalendarById(id);
        checkCalendarOwnership(calendar, user);

        calendarRepository.deleteById(id);
        log.info("캘린더 삭제 완료 - ID: {}", id);
    }

    // 캘린더 소유자 검증
    private void checkCalendarOwnership(Calendar calendar, SiteUser user) {
        if (!calendar.getUser().getId().equals(user.getId())) {
            throw new ServiceException("403", "캘린더 소유자만 접근할 수 있습니다.");
        }
    }
}
