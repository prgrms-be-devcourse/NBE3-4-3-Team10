package com.ll.TeamProject.domain.friend.repository

import com.ll.TeamProject.domain.friend.entity.Friendship
import com.ll.TeamProject.domain.user.entity.SiteUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FriendshipRepository : JpaRepository<Friendship, Long> {

    // ✅ 특정 사용자와 친구 관계 조회 (양방향 검색 지원)
    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user1 AND f.user2 = :user2) OR (f.user1 = :user2 AND f.user2 = :user1)")
    fun findFriendshipBetweenUsers(user1: SiteUser, user2: SiteUser): Optional<Friendship>

    // ✅ 친구 목록 조회 (ACCEPTED 상태만 반환)
    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user OR f.user2 = :user) AND f.status = 'ACCEPTED'")
    fun findAcceptedFriendshipsByUser(user: SiteUser): List<Friendship>

    // ✅ 받은 친구 요청 조회 (user2 기준)
    @Query("SELECT f FROM Friendship f WHERE f.user2 = :user AND f.status = 'PENDING'")
    fun findReceivedRequestsByUser(user: SiteUser): List<Friendship>

    // ✅ 보낸 친구 요청 조회 (user1 기준)
    @Query("SELECT f FROM Friendship f WHERE f.user1 = :user AND f.status = 'PENDING'")
    fun findSentRequestsByUser(user: SiteUser): List<Friendship>
}