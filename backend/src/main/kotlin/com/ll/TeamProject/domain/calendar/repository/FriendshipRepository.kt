package com.ll.TeamProject.domain.friend.repository

import com.ll.TeamProject.domain.friend.entity.Friendship
import com.ll.TeamProject.domain.user.entity.SiteUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FriendshipRepository : JpaRepository<Friendship, Long> {
    fun findByUser1OrUser2(user1: SiteUser, user2: SiteUser): List<Friendship>

    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user OR f.user2 = :user)")
    fun findAllByUser(user: SiteUser): List<Friendship>

    fun existsByUser1AndUser2(user1: SiteUser, user2: SiteUser): Boolean
}
