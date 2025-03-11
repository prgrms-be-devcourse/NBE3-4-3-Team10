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
        val user1 = userRepository.findById(userId1).orElseThrow { IllegalArgumentException(" 친구를 찾을 수 없습니다! ") }
        val user2 = userRepository.findById(userId2).orElseThrow { IllegalArgumentException(" 친구를 찾을 수 없습니다! ") }

        if (friendshipRepository.existsByUser1AndUser2(user1, user2)) {
            throw ServiceException("400","이미 등록된 친구입니다!")
        }

        val friendship = Friendship.create(user1, user2)
        return friendshipRepository.save(friendship)
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
        val user1 = userRepository.findById(userId1).orElseThrow { IllegalArgumentException(" 친구를 찾을 수 없습니다! ") }
        val user2 = userRepository.findById(userId2).orElseThrow { IllegalArgumentException(" 친구를 찾을 수 없습니다! ") }

        val friendship = friendshipRepository.findByUser1OrUser2(user1, user2)
            .find { (it.user1 == user1 && it.user2 == user2) || (it.user1 == user2 && it.user2 == user1) }
            ?: throw IllegalStateException("친구를 찾을 수 없습니다!")

        friendshipRepository.delete(friendship)
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
    fun shareCalendar(friendId: Long, calendarId: Long) {
        val friend = userRepository.findById(friendId).orElseThrow { IllegalArgumentException("친구를 찾을 수 없습니다!") }
        val calendar = calendarRepository.findById(calendarId).orElseThrow { IllegalArgumentException("캘린더를 찾을 수 없습니다!") }

        calendar.addSharedUser(friend)
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
