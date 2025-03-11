package com.ll.TeamProject.domain.friend.controller

import com.ll.TeamProject.domain.friend.dto.FriendResponseDto
import com.ll.TeamProject.domain.friend.service.FriendshipService
import com.ll.TeamProject.domain.user.entity.SiteUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = ["http://localhost:3000"], allowCredentials = "true")
class FriendshipController(
    private val friendshipService: FriendshipService
) {

    // 친구 추가
    @PostMapping("/add")
    fun addFriend(
        @AuthenticationPrincipal user: SiteUser,
        @RequestParam userId2: Long
    ): ResponseEntity<String> {
        val userId1 = user.id!!

        if (userId1 == userId2) {
            return ResponseEntity.badRequest().body("자기 자신과는 이미 친구입니다!")
        }

        val success = friendshipService.addFriend(userId1, userId2)
        return if (success) {
            ResponseEntity.ok("친구가 추가되었습니다!")
        } else {
            ResponseEntity.badRequest().body("이미 친구입니다!")
        }
    }

    // 친구 목록 조회
    @GetMapping
    fun getFriends(@AuthenticationPrincipal user: SiteUser): ResponseEntity<List<FriendResponseDto>> {
        val friends = friendshipService.getFriends(user.id!!)
        return ResponseEntity.ok(friends.map { FriendResponseDto.from(it) })
    }

    // 친구 삭제
    @DeleteMapping("/remove")
    fun removeFriend(
        @AuthenticationPrincipal user: SiteUser,
        @RequestParam userId2: Long
    ): ResponseEntity<String> {
        val userId1 = user.id!!

        val success = friendshipService.removeFriend(userId1, userId2)
        return if (success) {
            ResponseEntity.ok("친구가 삭제되었습니다!")
        } else {
            ResponseEntity.badRequest().body("친구 관계가 존재하지 않습니다!")
        }
    }
}