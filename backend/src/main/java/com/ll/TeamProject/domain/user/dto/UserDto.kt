package com.ll.TeamProject.domain.user.dto;

import com.ll.TeamProject.domain.user.entity.SiteUser;

import java.time.LocalDateTime;

public record UserDto(
        long id,
        String username,
        String nickname,
        String email,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        boolean locked
) {
    public UserDto(SiteUser user) {
        this(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getCreateDate(),
                user.getModifyDate(),
                user.isLocked()
        );
    }
}
