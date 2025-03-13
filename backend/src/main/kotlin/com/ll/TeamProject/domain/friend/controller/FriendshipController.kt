package com.ll.TeamProject.domain.friend.controller

import com.ll.TeamProject.domain.friend.dto.FriendResponseDto
import com.ll.TeamProject.domain.friend.service.FriendshipService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = ["http://localhost:3000"], allowCredentials = "true")
class FriendshipController(
    private val friendshipService: FriendshipService
) {
    /**
     * ✅ 친구 요청 보내기
     */
    @PostMapping("/request")
    fun sendFriendRequest(
        @RequestParam userId: Long,
        @RequestParam nickname: String
    ): ResponseEntity<String> {
        friendshipService.sendFriendRequest(userId, nickname)
        return ResponseEntity.ok("친구 요청이 전송되었습니다!")
    }

    /**
     * ✅ 사용자가 받은 친구 요청 목록 조회 (PENDING 상태)
     */
    @GetMapping("/{userId}/requests")
    fun getPendingRequests(@PathVariable userId: Long): ResponseEntity<List<FriendResponseDto>> {
        val requests = friendshipService.getPendingRequests(userId)
        return ResponseEntity.ok(requests)
    }

    /**
     * ✅ 친구 요청 수락
     */
    @PostMapping("/{userId}/accept/{requestId}")
    fun acceptFriendRequest(
        @PathVariable userId: Long,
        @PathVariable requestId: Long
    ): ResponseEntity<String> {
        friendshipService.acceptFriendRequest(userId, requestId)
        return ResponseEntity.ok("친구 요청이 수락되었습니다!")
    }

    /**
     * ✅ 친구 요청 거절
     */
    @PostMapping("/{userId}/decline/{requestId}")
    fun declineFriendRequest(
        @PathVariable userId: Long,
        @PathVariable requestId: Long
    ): ResponseEntity<String> {
        friendshipService.declineFriendRequest(userId, requestId)
        return ResponseEntity.ok("친구 요청이 거절되었습니다!")
    }

    /**
     * ✅ 친구 목록 조회
     */
    @GetMapping("/{userId}")
    fun getFriends(@PathVariable userId: Long): ResponseEntity<List<FriendResponseDto>> {
        val friends = friendshipService.getFriends(userId)
        return ResponseEntity.ok(friends)
    }

    /**
     * ✅ 친구 삭제 (양방향 삭제)
     */
    @DeleteMapping("/remove")
    fun removeFriend(
        @RequestParam userId1: Long,
        @RequestParam userId2: Long
    ): ResponseEntity<Void> {
        friendshipService.removeFriend(userId1, userId2)
        return ResponseEntity.noContent().build()
    }
}
