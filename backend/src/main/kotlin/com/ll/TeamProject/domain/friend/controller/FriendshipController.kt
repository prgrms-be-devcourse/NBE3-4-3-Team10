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
     * ✅ 친구 요청 보내기 (POST → @RequestBody 사용)
     */
    @PostMapping("/request")
    fun sendFriendRequest(@RequestBody request: Map<String, Any>): ResponseEntity<String> {
        val userId = (request["userId"] as Number).toLong()
        val friendNickname = request["nickname"] as String

        friendshipService.sendFriendRequest(userId, friendNickname)
        return ResponseEntity.ok("✅ 친구 요청을 보냈습니다.")
    }

    /**
     * ✅ 받은 친구 요청 목록 조회
     */
    @GetMapping("/{userId}/requests/received")
    fun getReceivedRequests(@PathVariable userId: Long): ResponseEntity<List<FriendRequestDto>> {
        val requests = friendshipService.getReceivedRequests(userId)
        return ResponseEntity.ok(requests)
    }

    /**
     * ✅ 보낸 친구 요청 목록 조회
     */
    @GetMapping("/{userId}/requests/sent")
    fun getSentRequests(@PathVariable userId: Long): ResponseEntity<List<FriendRequestDto>> {
        val requests = friendshipService.getSentRequests(userId)
        return ResponseEntity.ok(requests)
    }

    /**
     * ✅ 친구 요청 수락 (POST → @RequestBody 사용)
     */
    @PostMapping("/{userId}/accept")
    fun acceptFriendRequest(@PathVariable userId: Long, @RequestBody request: Map<String, Long>): ResponseEntity<String> {
        val requestId = request["requestId"] ?: throw IllegalArgumentException("requestId가 필요합니다.")
        friendshipService.acceptFriendRequest(userId, requestId)
        return ResponseEntity.ok("✅ 친구 요청을 수락했습니다.")
    }

    /**
     * ✅ 친구 요청 거절 (POST → @RequestBody 사용)
     */
    @PostMapping("/{userId}/decline")
    fun declineFriendRequest(@PathVariable userId: Long, @RequestBody request: Map<String, Long>): ResponseEntity<String> {
        val requestId = request["requestId"] ?: throw IllegalArgumentException("requestId가 필요합니다.")
        friendshipService.declineFriendRequest(userId, requestId)
        return ResponseEntity.ok("✅ 친구 요청을 거절했습니다.")
    }

    /**
     * ✅ 친구 요청 취소 (DELETE → @PathVariable 사용)
     */
    @DeleteMapping("/{userId}/cancel/{requestId}")
    fun cancelFriendRequest(@PathVariable userId: Long, @PathVariable requestId: Long): ResponseEntity<String> {
        friendshipService.cancelFriendRequest(userId, requestId)
        return ResponseEntity.ok("✅ 친구 요청을 취소했습니다.")
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
     * ✅ 친구 삭제 (DELETE → @PathVariable 사용)
     */
    @DeleteMapping("/{userId1}/remove/{userId2}")
    fun removeFriend(@PathVariable userId1: Long, @PathVariable userId2: Long): ResponseEntity<String> {
        friendshipService.removeFriend(userId1, userId2)
        return ResponseEntity.ok("✅ 친구를 삭제했습니다.")
    }
}
