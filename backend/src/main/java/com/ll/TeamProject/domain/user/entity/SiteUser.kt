package com.ll.TeamProject.domain.user.entity

import com.ll.TeamProject.domain.user.enums.Role
import com.ll.TeamProject.global.jpa.entity.BaseTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDateTime
import java.util.*

@Entity
class SiteUser(
    @Column(unique = true)
    var username: String,

    @Column(unique = true)
    var nickname: String,

    @Column
    var password: String,

    @Column(unique = true)
    var email: String,

    @Enumerated(EnumType.STRING)
    @Column
    var role: Role,

    @Column(unique = true)
    var apiKey: String,

    @Column
    var isDeleted: Boolean = false,

    @Column
    var deletedDate: LocalDateTime? = null,

    @Column
    var locked: Boolean = false
) : BaseTime() {

    protected constructor() : this("", "", "", "", Role.USER, "", false, null, false) // JPA 기본 생성자

    // TODO: 생성자 리팩토링 필요
    constructor(id: Long, username: String, nickname: String, role: Role) : this() {
        this.id = id
        this.username = username
        this.nickname = nickname
        this.role = role
    }

    constructor(id: Long, username: String) : this() {
        this.id = id
        this.username = username
    }

    constructor(username: String) : this() {
        this.username = username
    }

    constructor(username: String, password: String, nickname: String, email: String, role: Role, apiKey: String) : this() {
        this.username = username
        this.password = password
        this.nickname = nickname
        this.email = email
        this.role = role
        this.apiKey = apiKey
    }

    constructor(username: String, password: String, nickname: String, email: String, role: Role, apiKey: String, isDeleted: Boolean, deletedDate: LocalDateTime) : this() {
        this.username = username
        this.nickname = nickname
        this.password = password
        this.role = role
        this.email = email
        this.apiKey = apiKey
        this.isDeleted = isDeleted
        this.deletedDate = deletedDate
    }

    fun getAuthorities(): Collection<GrantedAuthority> =
        getAuthoritiesAsString().map { SimpleGrantedAuthority(it) }

    private fun getAuthoritiesAsString(): List<String> =
        mutableListOf<String>().apply {
            if (isAdmin()) add("ROLE_ADMIN")
        }

    fun isLocked(): Boolean = locked

    private fun isAdmin(): Boolean = this.role == Role.ADMIN

    fun changeNickname(newNickname: String) {
        this.nickname = newNickname
    }

    fun delete() {
        val randomSuffix = UUID.randomUUID()
        this.username = "deleted_$randomSuffix"
        this.email = "$username@deleted.com"
        changeNickname("탈퇴한 사용자_$username")
        this.isDeleted = true
        this.deletedDate = LocalDateTime.now()
    }

    fun lockAccount() {
        this.locked = true
    }

    fun unlockAccount() {
        this.locked = false
    }

    fun changePassword(password: String) {
        this.password = password
    }
}