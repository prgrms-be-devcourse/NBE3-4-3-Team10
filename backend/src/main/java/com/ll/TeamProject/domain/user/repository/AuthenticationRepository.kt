package com.ll.TeamProject.domain.user.repository;

import com.ll.TeamProject.domain.user.dto.DormantAccountProjection;
import com.ll.TeamProject.domain.user.entity.Authentication;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthenticationRepository extends JpaRepository<Authentication, Long> {
    Optional<Authentication> findByUserId(Long userId);

    @Query("""
        SELECT
            u.nickname AS nickname,
            u.email AS email
        FROM Authentication a
        JOIN a.user u
        WHERE a.lastLogin BETWEEN :startDate AND :endDate
          AND u.isDeleted = false
    """)
    List<DormantAccountProjection> findDormantAccountsInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}