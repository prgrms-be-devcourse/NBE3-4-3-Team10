package com.ll.TeamProject.domain.friend.service

import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.calendar.repository.CalendarRepository
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
    private val userRepository: UserRepository,
    private val calendarRepository: CalendarRepository
) {
    /**
     * 친구 추가
     */
    fun addFriend(userId1: Long, userId2: Long): Friendship {
        if (userId1 == userId2) {
            throw IllegalArgumentException("❌ 본인은 친구 추가할 수 없습니다!")
        }

        val user1 = userRepository.findById(userId1)
            .orElseThrow { IllegalArgumentException("❌ 친구를 찾을 수 없습니다! (userId1: $userId1)") }
        val user2 = userRepository.findById(userId2)
            .orElseThrow { IllegalArgumentException("❌ 친구를 찾을 수 없습니다! (userId2: $userId2)") }

        if (friendshipRepository.existsByUser1AndUser2(user1, user2)) {
            throw IllegalStateException("❌ 이미 친구로 추가된 사용자입니다!")
        }

        val friendship = Friendship(user1 = user1, user2 = user2)
        return friendshipRepository.save(friendship) // ✅ 리턴 타입이 Friendship이므로 명시 가능
    }


    /**
     * 친구 목록 조회
     */
    fun getFriends(userId: Long): List<SiteUser> {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException(" 친구를 찾을 수 없습니다! ") }
        return friendshipRepository.findAllByUser(user)
            .map { if (it.user1 == user) it.user2 else it.user1 }
    }

    /**
     * 친구 삭제
     */
    fun removeFriend(userId1: Long, userId2: Long) {
        val user1 = userRepository.findById(userId1)
            .orElseThrow { IllegalArgumentException("❌ 친구를 찾을 수 없습니다! (userId1: $userId1)") }
        val user2 = userRepository.findById(userId2)
            .orElseThrow { IllegalArgumentException("❌ 친구를 찾을 수 없습니다! (userId2: $userId2)") }

        println("✅ 찾은 친구 정보: ${user1.username} <-> ${user2.username}")

        // ✅ 친구 관계 양방향 조회
        val friendship = friendshipRepository.findFriendshipBetweenUsers(user1, user2)
            ?: throw IllegalStateException("친구 관계가 존재하지 않습니다!")

        friendshipRepository.delete(friendship)
        println("✅ 친구 삭제 완료: userId1=$userId1, userId2=$userId2")
    }


    /**
     * 사용자가 공유받은 캘린더 목록 조회
     */
    fun getSharedCalendars(userId: Long): List<Calendar> {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다!") }
        return calendarRepository.findSharedCalendarsByUser(user)
    }

    /**
     * 특정 친구에게 캘린더 공유
     */
    fun shareCalendar(friendId: Long, calendarId: Long, ownerId: Long) {
        val friend = userRepository.findById(friendId).orElseThrow { IllegalArgumentException("친구를 찾을 수 없습니다!") }
        val calendar = calendarRepository.findById(calendarId).orElseThrow { IllegalArgumentException("캘린더를 찾을 수 없습니다!") }

        calendar.addSharedUser(friend, owner)
        calendarRepository.save(calendar)
    }

    /**
     * 특정 친구와의 캘린더 공유 해제
     */
    fun unshareCalendar(friendId: Long, calendarId: Long) {
        val friend = userRepository.findById(friendId).orElseThrow { IllegalArgumentException("친구를 찾을 수 없습니다!") }
        val calendar = calendarRepository.findById(calendarId).orElseThrow { IllegalArgumentException("캘린더를 찾을 수 없습니다!") }

        calendar.removeSharedUser(friend)
        calendarRepository.save(calendar)
    }
}
