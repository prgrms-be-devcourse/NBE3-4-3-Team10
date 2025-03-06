package com.ll.TeamProject.domain.user.repository

import com.ll.TeamProject.domain.user.dto.DormantAccountProjection
import com.ll.TeamProject.domain.user.entity.Authentication
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.*

interface AuthenticationRepository : JpaRepository<Authentication, Long> {
    fun findByUserId(userId: Long): Optional<Authentication>

    @Query(
        """
        SELECT
            u.nickname AS nickname,
            u.email AS email
        FROM Authentication a
        JOIN a.user u
        WHERE a.lastLogin BETWEEN :startDate AND :endDate
          AND u.isDeleted = false
        """
    )
    fun findDormantAccountsInDateRange(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<DormantAccountProjection>
}