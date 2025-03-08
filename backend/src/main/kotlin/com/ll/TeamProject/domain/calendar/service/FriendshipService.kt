package com.ll.TeamProject.domain.friend.service

import com.ll.TeamProject.domain.friend.entity.Friendship
import com.ll.TeamProject.domain.friend.repository.FriendshipRepository
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.repository.UserRepository
import com.ll.TeamProject.global.exceptions.ServiceException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class FriendshipService(
    private val friendshipRepository: FriendshipRepository,
    private val userRepository: UserRepository
) {
    fun addFriend(userId1: Long, userId2: Long): Friendship {
        val user1 = userRepository.findById(userId1).orElseThrow { IllegalArgumentException("User not found") }
        val user2 = userRepository.findById(userId2).orElseThrow { IllegalArgumentException("User not found") }

        if (friendshipRepository.existsByUser1AndUser2(user1, user2)) {
            throw ServiceException("400","Users are already friends")
        }

        val friendship = Friendship.create(user1, user2)
        return friendshipRepository.save(friendship)
    }

    fun getFriends(userId: Long): List<SiteUser> {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found") }
        return friendshipRepository.findAllByUser(user)
            .map { if (it.user1 == user) it.user2 else it.user1 }
    }

    fun removeFriend(userId1: Long, userId2: Long) {
        val user1 = userRepository.findById(userId1).orElseThrow { IllegalArgumentException("User not found") }
        val user2 = userRepository.findById(userId2).orElseThrow { IllegalArgumentException("User not found") }

        val friendship = friendshipRepository.findByUser1OrUser2(user1, user2)
            .find { (it.user1 == user1 && it.user2 == user2) || (it.user1 == user2 && it.user2 == user1) }
            ?: throw IllegalStateException("Friendship not found")

        friendshipRepository.delete(friendship)
    }
}
