package com.ll.TeamProject.domain.friend.service

import com.ll.TeamProject.domain.friend.dto.FriendRequestDto
import com.ll.TeamProject.domain.friend.dto.FriendResponseDto
import com.ll.TeamProject.domain.friend.entity.Friendship
import com.ll.TeamProject.domain.friend.entity.FriendshipStatus
import com.ll.TeamProject.domain.friend.repository.FriendshipRepository
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
     * ✅ 친구 요청 보내기 (중복 검사 추가)
     */
    fun sendFriendRequest(userId: Long, friendNickname: String) {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        val friend = userRepository.findByNickname(friendNickname)
            ?: throw IllegalArgumentException("해당 유저를 찾을 수 없습니다!")

        if (friend == user) throw IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다!")

        // 🚀 중복 검사 추가: 이미 친구 관계인지 확인
        val existingFriendship = friendshipRepository.findFriendshipBetweenUsers(user, friend)
        if (existingFriendship != null) {
            throw IllegalArgumentException("이미 친구이거나 요청을 보낸 상태입니다!")
        }

        val friendship = Friendship.create(user, friend, friendshipRepository)
        friendshipRepository.save(friendship)
    }

    /**
     * ✅ 친구 요청 목록 조회 (PENDING 상태만)
     */
    fun getPendingRequests(userId: Long): List<FriendRequestDto> {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        return friendshipRepository.findByUser2AndStatus(user, FriendshipStatus.PENDING)
            .map { FriendRequestDto.from(it) }
    }

    /**
     * ✅ 친구 요청 수락 (이미 수락된 요청인지 확인)
     */
    fun acceptFriendRequest(userId: Long, requestId: Long) {
        val friendship = friendshipRepository.findById(requestId)
            .orElseThrow { IllegalArgumentException("친구 요청을 찾을 수 없습니다!") }

        if (friendship.user2.id != userId) {
            throw IllegalArgumentException("본인에게 온 요청만 수락할 수 있습니다!")
        }

        // 🚀 예외 처리 추가: 이미 수락된 요청인지 확인
        if (friendship.status == FriendshipStatus.ACCEPTED) {
            throw IllegalArgumentException("이미 수락된 친구 요청입니다!")
        }

        friendship.acceptRequest()
        friendshipRepository.save(friendship)
    }

    /**
     * ✅ 친구 요청 거절 (이미 거절된 요청인지 확인)
     */
    fun declineFriendRequest(userId: Long, requestId: Long) {
        val friendship = friendshipRepository.findById(requestId)
            .orElseThrow { IllegalArgumentException("친구 요청을 찾을 수 없습니다!") }

        if (friendship.user2.id != userId) {
            throw IllegalArgumentException("본인에게 온 요청만 거절할 수 있습니다!")
        }

        // 🚀 예외 처리 추가: 이미 거절된 요청인지 확인
        if (friendship.status == FriendshipStatus.DECLINED) {
            throw IllegalArgumentException("이미 거절된 친구 요청입니다!")
        }

        friendship.declineRequest()
        friendshipRepository.save(friendship)
    }

    /**
     * ✅ 친구 요청 취소 기능 (추가된 부분)
     */
    fun cancelFriendRequest(userId: Long, requestId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        val friendship = friendshipRepository.findById(requestId)
            .orElseThrow { IllegalArgumentException("친구 요청을 찾을 수 없습니다!") }

        if (friendship.user1.id != userId) {
            throw IllegalArgumentException("본인이 보낸 요청만 취소할 수 있습니다!")
        }

        friendshipRepository.delete(friendship) // 🚀 요청 삭제
    }

    /**
     * ✅ 친구 목록 조회 (ACCEPTED 상태만)
     */
    fun getFriends(userId: Long): List<FriendResponseDto> {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        return friendshipRepository.findAcceptedFriendshipsByUser(user)
            .map { friendship -> FriendResponseDto.from(friendship, user.id!!) }
    }

    /**
     * ✅ 친구 삭제 (양방향 친구 관계 확인 후 삭제)
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
