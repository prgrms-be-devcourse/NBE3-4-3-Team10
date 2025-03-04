package com.ll.TeamProject.domain.calendar.entity;

import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.global.jpa.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class Message extends BaseTime {
    // BaseTime : id (BaseEntity, no setter), 생성/수정일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 작성자
    private SiteUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    private String content; // 메시지 내용

    private boolean isRead; // 읽음 여부
}