package com.ll.TeamProject.domain.user.entity;

import com.ll.TeamProject.global.jpa.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForbiddenNickname extends BaseTime {
    // BaseTime : id (BaseEntity, no setter), 생성/수정일

    @Column(unique = true)
    private String forbiddenName;
}
