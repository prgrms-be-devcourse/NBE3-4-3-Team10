package com.ll.TeamProject.domain.calendar.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.TeamProject.domain.calendar.dto.CalendarUpdateDto;
import com.ll.TeamProject.domain.schedule.entity.Schedule;
import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Calendar extends BaseTime {

    // 캘린더 소유 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SiteUser user;

    // 캘린더 이름
    private String name;

    // 캘린더 설명
    private String description;

    // 공유된 사용자 목록
    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SharedCalendar> sharedUsers = new ArrayList<>();

    // 메시지 목록
    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Message> messageList = new ArrayList<>();

    // 일정 목록
    @OneToMany(mappedBy = "calendar", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Schedule> schedules = new ArrayList<>();

    // 비즈니스 생성자
    public Calendar(SiteUser user, String name, String description) {
        this.user = user;
        this.name = name;
        this.description = description;
    }

    // 캘린더 정보 업데이트 (record 적용)
    public void update(CalendarUpdateDto updateDto) {
        this.name = updateDto.name();
        this.description = updateDto.description();
    }

    // 캘린더 이름 및 설명 변경
    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
