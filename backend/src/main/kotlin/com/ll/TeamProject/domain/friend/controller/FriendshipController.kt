package com.ll.TeamProject.domain.friend.controller

import com.ll.TeamProject.domain.friend.dto.FriendRequestDto
import com.ll.TeamProject.domain.friend.dto.FriendResponseDto
import com.ll.TeamProject.domain.friend.service.FriendshipService
import com.ll.TeamProject.domain.user.entity.SiteUser
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
    fun sendFriendRequest(
        @AuthenticationPrincipal user: SiteUser,
        @RequestBody request: Map<String, Any>
    ): ResponseEntity<String> {
        val userId = user.id!!
        val friendNickname = request["nickname"] as String

        friendshipService.sendFriendRequest(userId, friendNickname)
        return ResponseEntity.ok("✅ 친구 요청을 보냈습니다.")
    }

    /**
     * ✅ 받은 친구 요청 목록 조회
     */
    @GetMapping("/requests/received")
    fun getReceivedRequests(@AuthenticationPrincipal user: SiteUser): ResponseEntity<List<FriendRequestDto>> {
        val requests = friendshipService.getReceivedRequests(user.id!!)
        return ResponseEntity.ok(requests)
    }

    /**
     * ✅ 보낸 친구 요청 목록 조회
     */
    @GetMapping("/requests/sent")
    fun getSentRequests(@AuthenticationPrincipal user: SiteUser): ResponseEntity<List<FriendRequestDto>> {
        val requests = friendshipService.getSentRequests(user.id!!)
        return ResponseEntity.ok(requests)
    }

    /**
     * ✅ 친구 요청 수락 (POST → @RequestBody 사용)
     */
    @PostMapping("/accept")
    fun acceptFriendRequest(
        @AuthenticationPrincipal user: SiteUser,
        @RequestBody request: Map<String, Long>
    ): ResponseEntity<String> {
        val requestId = request["requestId"] ?: throw IllegalArgumentException("requestId가 필요합니다.")
        friendshipService.acceptFriendRequest(user.id!!, requestId)
        return ResponseEntity.ok("✅ 친구 요청을 수락했습니다.")
    }

    /**
     * ✅ 친구 요청 거절 (POST → @RequestBody 사용)
     */
    @PostMapping("/decline")
    fun declineFriendRequest(
        @AuthenticationPrincipal user: SiteUser,
        @RequestBody request: Map<String, Long>
    ): ResponseEntity<String> {
        val requestId = request["requestId"] ?: throw IllegalArgumentException("requestId가 필요합니다.")
        friendshipService.declineFriendRequest(user.id!!, requestId)
        return ResponseEntity.ok("✅ 친구 요청을 거절했습니다.")
    }

    /**
     * ✅ 친구 요청 취소 (DELETE → @PathVariable 사용)
     */
    @DeleteMapping("/cancel/{requestId}")
    fun cancelFriendRequest(
        @AuthenticationPrincipal user: SiteUser,
        @PathVariable requestId: Long
    ): ResponseEntity<String> {
        friendshipService.cancelFriendRequest(user.id!!, requestId)
        return ResponseEntity.ok("✅ 친구 요청을 취소했습니다.")
    }

    /**
     * ✅ 친구 목록 조회 (ACCEPTED 상태만)
     */
    @GetMapping("/list")
    fun getFriends(@AuthenticationPrincipal user: SiteUser): ResponseEntity<List<FriendResponseDto>> {
        val friends = friendshipService.getFriends(user.id!!)
        return ResponseEntity.ok(friends)
    }

    /**
     * ✅ 친구 삭제 (DELETE → @PathVariable 사용)
     */
    @DeleteMapping("/remove/{userId2}")
    fun removeFriend(
        @AuthenticationPrincipal user: SiteUser,
        @PathVariable userId2: Long
    ): ResponseEntity<String> {
        friendshipService.removeFriend(user.id!!, userId2)
        return ResponseEntity.ok("✅ 친구를 삭제했습니다.")
    }
}
