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
    fun addFriend(userId1: Long, userId2: Long): Boolean {
        val user1 = userRepository.findById(userId1).orElseThrow {
            ServiceException("404", "친구 $userId1 를 찾을 수 없습니다!")
        }
        val user2 = userRepository.findById(userId2).orElseThrow {
            ServiceException("404", "친구 $userId2 를 찾을 수 없습니다!")
        }

        val user1Id = user1.id ?: throw ServiceException("400", "친구의 ID가 존재하지 않습니다!")
        val user2Id = user2.id ?: throw ServiceException("400", "친구의 ID가 존재하지 않습니다!")

        val (firstUser, secondUser) = if (user1Id < user2Id) user1 to user2 else user2 to user1

        if (friendshipRepository.existsByUser1AndUser2(firstUser, secondUser)) {
            return false // 이미 친구 상태
        }

        val friendship = Friendship.create(firstUser, secondUser)
        friendshipRepository.save(friendship)
        return true // 친구 추가 성공
    }

    fun getFriends(userId: Long): List<SiteUser> {
        val user = userRepository.findById(userId).orElseThrow {
            ServiceException("404", "친구를 찾을 수 없습니다!")
        }
        return friendshipRepository.findAllByUser(user)
            .map { friendship -> if (friendship.user1 == user) friendship.user2 else friendship.user1 }
    }

    fun removeFriend(userId1: Long, userId2: Long): Boolean {
        val user1 = userRepository.findById(userId1).orElseThrow {
            ServiceException("404", "친구를 찾을 수 없습니다!")
        }
        val user2 = userRepository.findById(userId2).orElseThrow {
            ServiceException("404", "친구를 찾을 수 없습니다!")
        }

        val friendship = friendshipRepository.findByUser1AndUser2OrUser2AndUser1(user1, user2, user2, user1)
            ?: return false // 친구 관계 없음

        friendshipRepository.delete(friendship)
        return true
    }
}