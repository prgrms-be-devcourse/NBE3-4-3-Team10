package com.ll.TeamProject.domain.friend.controller

import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.friend.dto.FriendResponseDto
import com.ll.TeamProject.domain.friend.service.FriendshipService
import com.ll.TeamProject.domain.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = ["http://localhost:3000"], allowCredentials = "true")
class FriendshipController(
    private val friendshipService: FriendshipService,
    private val userService: UserService
) {

    @PostMapping("/add")
    fun addFriend(@RequestParam userId1: Long, @RequestParam userId2: Long): ResponseEntity<String> {
        friendshipService.addFriend(userId1, userId2)
        return ResponseEntity.ok(" 친구가 추가됬어요! ")
    }

    @GetMapping("/{userId}")
    fun getFriends(@PathVariable userId: Long): ResponseEntity<List<FriendResponseDto>> {
        val friends = friendshipService.getFriends(userId)
        val response = friends.map { FriendResponseDto.from(it) }
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/remove")
    fun removeFriend(@RequestParam userId1: Long, @RequestParam userId2: Long): ResponseEntity<String> {
        friendshipService.removeFriend(userId1, userId2)
        return ResponseEntity.ok(" 친구가 삭제됬어요! ")
    }
    /**
     * 📌 친구가 공유한 캘린더 목록 조회 API
     */
    @GetMapping("/{userId}/shared-calendars")
    fun getSharedCalendars(@PathVariable userId: Long): ResponseEntity<List<Calendar>> {
        val sharedCalendars = friendshipService.getSharedCalendars(userId)
        return ResponseEntity.ok(sharedCalendars)
    }

    /**
     * 📌 특정 친구에게 캘린더 공유 요청 API
     */
    @PostMapping("/{friendId}/share-calendar")
    fun shareCalendar(@PathVariable friendId: Long, @RequestParam calendarId: Long): ResponseEntity<String> {
        friendshipService.shareCalendar(friendId, calendarId)
        return ResponseEntity.ok("캘린더 공유가 완료되었습니다.")
    }

    /**
     * 📌 특정 친구와의 캘린더 공유 해제 API
     */
    @DeleteMapping("/{friendId}/unshare-calendar")
    fun unshareCalendar(@PathVariable friendId: Long, @RequestParam calendarId: Long): ResponseEntity<String> {
        friendshipService.unshareCalendar(friendId, calendarId)
        return ResponseEntity.ok("캘린더 공유가 해제되었습니다.")
    }
}
