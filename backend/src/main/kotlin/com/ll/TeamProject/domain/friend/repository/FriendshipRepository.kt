package com.ll.TeamProject.domain.friend.repository

import com.ll.TeamProject.domain.friend.entity.Friendship
import com.ll.TeamProject.domain.friend.entity.FriendshipStatus
import com.ll.TeamProject.domain.user.entity.SiteUser
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FriendshipRepository : JpaRepository<Friendship, Long> {

    // ✅ user1 → user2 관계 조회
    fun findByUser1AndUser2(user1: SiteUser, user2: SiteUser): Friendship?

    // ✅ user2 → user1 관계 조회 (역방향)
    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user1 AND f.user2 = :user2) OR (f.user1 = :user2 AND f.user2 = :user1)")
    fun findFriendshipBetweenUsers(
        @Param("user1") user1: SiteUser,
        @Param("user2") user2: SiteUser
    ): Friendship?

    // ✅ 특정 사용자의 친구 목록 조회 (ACCEPTED 상태만 조회)
    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user OR f.user2 = :user) AND f.status = 'ACCEPTED'")
    fun findAcceptedFriendshipsByUser(@Param("user") user: SiteUser): List<Friendship>

    fun existsByUser1AndUser2(user1: SiteUser, user2: SiteUser): Boolean

    fun findByUser2AndStatus(user2: SiteUser, status: FriendshipStatus): List<Friendship>
}
