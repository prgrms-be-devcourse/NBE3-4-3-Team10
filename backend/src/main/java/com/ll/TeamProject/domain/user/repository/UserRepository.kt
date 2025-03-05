package com.ll.TeamProject.domain.user.repository;

import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.domain.user.enums.Role;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findByUsername(String username);

    Optional<SiteUser> findByApiKey(String apiKey);

    Page<SiteUser> findByRoleAndEmailLikeAndIsDeletedFalse(Role role, String emailLike, PageRequest pageRequest);

    Page<SiteUser> findByRoleAndUsernameLikeAndIsDeletedFalse(Role role, String usernameLike, PageRequest pageRequest);

    Optional<SiteUser> findByUsernameAndEmail(String username, String email);

    Page<SiteUser> findByRoleAndIsDeletedFalse(Role role, PageRequest pageRequest);

    Optional<SiteUser> findByEmail(String email);

    @Query("""
        SELECT a.user.id
        FROM Authentication a
        JOIN a.user u
        WHERE a.lastLogin BETWEEN :startDate AND :endDate
        AND u.isDeleted = false
    """)
    List<Long> findUserIdsInDateRange(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Modifying
    @Query("UPDATE SiteUser u SET u.locked = true WHERE u.id IN :ids")
    void bulkLockAccounts(@Param("ids") List<Long> ids);

    @Modifying
    @Query("""
    UPDATE SiteUser u
    SET u.username = CONCAT('deleted_', UUID()), 
        u.email = CONCAT('deleted_', UUID(), '@deleted.com'), 
        u.nickname = CONCAT('탈퇴한 사용자_', u.username), 
        u.isDeleted = true, 
        u.deletedDate = :deletedDate
    WHERE u.id IN :userIds
""")
    void bulkDeleteAccounts(@Param("userIds") List<Long> userIds, @Param("deletedDate") LocalDateTime deletedDate);
}