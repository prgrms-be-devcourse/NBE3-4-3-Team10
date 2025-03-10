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
        val user1 = userRepository.findById(userId1).orElseThrow { IllegalArgumentException(" 친구를 찾을 수 없습니다! ") }
        val user2 = userRepository.findById(userId2).orElseThrow { IllegalArgumentException(" 친구를 찾을 수 없습니다! ") }

        if (friendshipRepository.existsByUser1AndUser2(user1, user2)) {
            throw ServiceException("400","이미 등록된 친구입니다!")
        }

        val friendship = Friendship.create(user1, user2)
        return friendshipRepository.save(friendship)
    }

    fun getFriends(userId: Long): List<SiteUser> {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException(" 친구를 찾을 수 없습니다! ") }
        return friendshipRepository.findAllByUser(user)
            .map { if (it.user1 == user) it.user2 else it.user1 }
    }

    fun removeFriend(userId1: Long, userId2: Long) {
        val user1 = userRepository.findById(userId1).orElseThrow { IllegalArgumentException(" 친구를 찾을 수 없습니다! ") }
        val user2 = userRepository.findById(userId2).orElseThrow { IllegalArgumentException(" 친구를 찾을 수 없습니다! ") }

        val friendship = friendshipRepository.findByUser1OrUser2(user1, user2)
            .find { (it.user1 == user1 && it.user2 == user2) || (it.user1 == user2 && it.user2 == user1) }
            ?: throw IllegalStateException("친구를 찾을 수 없습니다!")

        friendshipRepository.delete(friendship)
    }
}
