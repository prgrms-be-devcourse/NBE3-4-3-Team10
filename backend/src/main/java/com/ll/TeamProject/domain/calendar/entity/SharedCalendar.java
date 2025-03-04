package com.ll.TeamProject.domain.calendar.entity;

import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class SharedCalendar extends BaseEntity {
    // BaseEntity : id (no setter)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar; // 캘린더 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SiteUser user; // 공유받은 사용자 ID
}