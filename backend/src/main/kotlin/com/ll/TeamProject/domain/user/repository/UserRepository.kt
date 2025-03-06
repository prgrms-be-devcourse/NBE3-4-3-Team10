package com.ll.TeamProject.domain.user.repository

import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.enums.Role
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.*

interface UserRepository : JpaRepository<SiteUser, Long> {
    fun findByUsername(username: String): Optional<SiteUser>

    fun findByApiKey(apiKey: String): Optional<SiteUser>

    fun findByRoleAndEmailLikeAndIsDeletedFalse(
        role: Role,
        emailLike: String,
        pageRequest: PageRequest
    ): Page<SiteUser>

    fun findByRoleAndUsernameLikeAndIsDeletedFalse(
        role: Role,
        usernameLike: String,
        pageRequest: PageRequest
    ): Page<SiteUser>

    fun findByRoleAndIsDeletedFalse(role: Role, pageRequest: PageRequest): Page<SiteUser>

    fun findByEmail(email: String): Optional<SiteUser>

    @Query(
        """
        SELECT a.user.id
        FROM Authentication a
        JOIN a.user u
        WHERE a.lastLogin BETWEEN :startDate AND :endDate
        AND u.isDeleted = false
        """
    )
    fun findUserIdsInDateRange(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Long>

    @Modifying
    @Query("UPDATE SiteUser u SET u.locked = true WHERE u.id IN :ids")
    fun bulkLockAccounts(@Param("ids") ids: List<Long>)

    @Modifying
    @Query(
        """
        UPDATE SiteUser u
        SET u.username = CONCAT('deleted_', UUID()), 
            u.email = CONCAT('deleted_', UUID(), '@deleted.com'), 
            u.nickname = CONCAT('탈퇴한 사용자_', u.username), 
            u.isDeleted = true, 
            u.deletedDate = :deletedDate
        WHERE u.id IN :userIds
        """
    )
    fun bulkDeleteAccounts(@Param("userIds") userIds: List<Long>,
                           @Param("deletedDate") deletedDate: LocalDateTime)
}