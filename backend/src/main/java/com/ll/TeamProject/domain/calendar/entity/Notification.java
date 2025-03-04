package com.ll.TeamProject.domain.calendar.entity;

import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.global.jpa.entity.BaseTime;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class Notification extends BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SiteUser user; // 알림 대상 사용자 (FK)

    private String content; // 알림 내용
    private boolean isRead = false; // 읽음 여부
}