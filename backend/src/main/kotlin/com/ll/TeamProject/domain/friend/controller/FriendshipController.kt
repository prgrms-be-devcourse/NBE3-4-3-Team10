package com.ll.TeamProject.domain.friend.controller

import com.ll.TeamProject.domain.friend.dto.FriendRequestDto
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
     * ✅ 친구 요청 목록 조회 (PENDING 상태)
     */
    @GetMapping("/{userId}/requests")
    fun getPendingRequests(@PathVariable userId: Long): ResponseEntity<List<FriendRequestDto>> {
        val requests = friendshipService.getPendingRequests(userId)
        return ResponseEntity.ok(requests)
    }

    /**
     * ✅ 친구 요청 수락
     */
    @PostMapping("/{userId}/accept")
    fun acceptFriendRequest(
        @PathVariable userId: Long,
        @RequestParam requestId: Long
    ): ResponseEntity<String> {
        friendshipService.acceptFriendRequest(userId, requestId)
        return ResponseEntity.ok("친구 요청을 수락하였습니다!")
    }

    /**
     * ✅ 친구 요청 거절
     */
    @PostMapping("/{userId}/decline")
    fun declineFriendRequest(
        @PathVariable userId: Long,
        @RequestParam requestId: Long
    ): ResponseEntity<String> {
        friendshipService.declineFriendRequest(userId, requestId)
        return ResponseEntity.ok("친구 요청을 거절하였습니다!")
    }

    /**
     * ✅ 친구 요청 취소 (새로 추가됨)
     */
    @DeleteMapping("/{userId}/cancel")
    fun cancelFriendRequest(
        @PathVariable userId: Long,
        @RequestParam requestId: Long
    ): ResponseEntity<String> {
        friendshipService.cancelFriendRequest(userId, requestId)
        return ResponseEntity.ok("친구 요청이 취소되었습니다!")
    }

    /**
     * ✅ 친구 목록 조회 (ACCEPTED 상태만)
     */
    @GetMapping("/{userId}/list")
    fun getFriends(@PathVariable userId: Long): ResponseEntity<List<FriendResponseDto>> {
        val friends = friendshipService.getFriends(userId)
        return ResponseEntity.ok(friends)
    }

    /**
     * ✅ 친구 삭제
     */
    @DeleteMapping("/{userId1}/remove/{userId2}")
    fun removeFriend(
        @PathVariable userId1: Long,
        @PathVariable userId2: Long
    ): ResponseEntity<String> {
        friendshipService.removeFriend(userId1, userId2)
        return ResponseEntity.ok("친구가 삭제되었습니다!")
    }
}
