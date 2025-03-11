package com.ll.TeamProject.domain.friend.repository

import com.ll.TeamProject.domain.friend.entity.Friendship
import com.ll.TeamProject.domain.user.entity.SiteUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FriendshipRepository : JpaRepository<Friendship, Long> {
    @Query("SELECT f FROM Friendship f WHERE f.user1 = :user OR f.user2 = :user")
    fun findAllByUser(user: SiteUser): List<Friendship>

    fun existsByUser1AndUser2(user1: SiteUser, user2: SiteUser): Boolean

    fun findByUser1AndUser2(user1: SiteUser, user2: SiteUser): Friendship?

    fun findByUser1AndUser2OrUser2AndUser1(user1: SiteUser, user2: SiteUser, user2Alt: SiteUser, user1Alt: SiteUser): Friendship?
}