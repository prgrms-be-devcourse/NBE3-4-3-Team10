package com.ll.TeamProject.domain.friend.service

import com.ll.TeamProject.domain.friend.dto.FriendResponseDto
import com.ll.TeamProject.domain.friend.entity.Friendship
import com.ll.TeamProject.domain.friend.entity.FriendshipStatus
import com.ll.TeamProject.domain.friend.repository.FriendshipRepository
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class FriendshipService(
    private val friendshipRepository: FriendshipRepository,
    private val userRepository: UserRepository
) {
    /**
     * ✅ 친구 요청 보내기
     */
    fun sendFriendRequest(userId: Long, friendNickname: String) {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        val friend = userRepository.findByNickname(friendNickname)
            ?: throw IllegalArgumentException("해당 유저를 찾을 수 없습니다!")

        if (friend == user) throw IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다!")

        val friendship = Friendship.create(user, friend, friendshipRepository)
        friendshipRepository.save(friendship)
    }

    /**
     * ✅ 친구 요청 목록 조회 (PENDING 상태)
     */
    fun getPendingRequests(userId: Long): List<FriendResponseDto> {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        return friendshipRepository.findByUser2AndStatus(user, FriendshipStatus.PENDING)
            .map { FriendResponseDto.from(it, userId) }
    }

    /**
     * ✅ 친구 요청 수락
     */
    fun acceptFriendRequest(userId: Long, requestId: Long) {
        val friendship = friendshipRepository.findById(requestId)
            .orElseThrow { IllegalArgumentException("친구 요청을 찾을 수 없습니다!") }

        if (friendship.user2.id != userId) {
            throw IllegalArgumentException("본인에게 온 요청만 수락할 수 있습니다!")
        }

        friendship.acceptRequest()
        friendshipRepository.save(friendship)
    }

    /**
     * ✅ 친구 요청 거절
     */
    fun declineFriendRequest(userId: Long, requestId: Long) {
        val friendship = friendshipRepository.findById(requestId)
            .orElseThrow { IllegalArgumentException("친구 요청을 찾을 수 없습니다!") }

        if (friendship.user2.id != userId) {
            throw IllegalArgumentException("본인에게 온 요청만 거절할 수 있습니다!")
        }

        friendship.declineRequest()
        friendshipRepository.save(friendship)
    }

    // ✅ 친구 요청 취소 기능
    fun cancelFriendRequest(userId: Long, requestId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        val friendship = friendshipRepository.findById(requestId)
            .orElseThrow { IllegalArgumentException("친구 요청을 찾을 수 없습니다!") }

        friendship.cancelRequest(user)
        friendshipRepository.save(friendship)
    }

    /**
     * ✅ 친구 목록 조회 (FriendResponseDto 반환)
     */
    fun getFriends(userId: Long): List<FriendResponseDto> {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        return friendshipRepository.findAllByUser(user)
            .filter { it.status == FriendshipStatus.ACCEPTED } // ✅ ACCEPTED 상태만 필터링
            .map { friendship -> FriendResponseDto.from(friendship, user.id!!) }
    }

    /**
     * ✅ 친구 삭제
     */
    fun removeFriend(userId1: Long, userId2: Long) {
        val user1 = userRepository.findById(userId1)
            .orElseThrow { IllegalArgumentException("❌ 친구를 찾을 수 없습니다! (userId1: $userId1)") }
        val user2 = userRepository.findById(userId2)
            .orElseThrow { IllegalArgumentException("❌ 친구를 찾을 수 없습니다! (userId2: $userId2)") }

        val friendship = friendshipRepository.findFriendshipBetweenUsers(user1, user2)
            ?: throw IllegalStateException("친구 관계가 존재하지 않습니다!")

        friendshipRepository.delete(friendship)
    }
}
