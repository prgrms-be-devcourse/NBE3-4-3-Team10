package com.ll.TeamProject.domain.user.entity;

import com.ll.TeamProject.domain.user.enums.Role;
import com.ll.TeamProject.global.jpa.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteUser extends BaseTime {
    // BaseTime : id (BaseEntity, no setter), 생성/수정일

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String nickname;

    @Column
    private String password;

    @Column(unique = true)
    private String email;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique = true)
    private String apiKey;

    @Column
    private boolean isDeleted;

    @Column
    private LocalDateTime deletedDate;

    @Column
    private boolean locked;

    public SiteUser(long id, String username, String nickname, Role role) {
        super();
        this.setId(id);
        this.username = username;
        this.nickname = nickname;
        this.role = role;
    }

    public SiteUser(long id, String username, Role role) {
        super();
        this.setId(id);
        this.username = username;
        this.role = role;
    }

    public SiteUser(long id, String username) {
        super();
        this.setId(id);
        this.username = username;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthoritiesAsString()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private List<String> getAuthoritiesAsString() {
        List<String> authorities = new ArrayList<>();

        if (isAdmin()) authorities.add("ROLE_ADMIN");

        return authorities;
    }

    private boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void delete() {
        this.username = "deleted_" + UUID.randomUUID();
        this.email = username + "@deleted.com";
        changeNickname("탈퇴한 사용자_" + username);
        this.isDeleted = true;
        this.deletedDate = LocalDateTime.now();
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void lockAccountAndResetPassword(String randomPassword) {
        this.locked = true;
        this.password = randomPassword;
    }

    public void lockAccount() {
        this.locked = true;
    }

    public void unlockAccount() {
        this.locked = false;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}