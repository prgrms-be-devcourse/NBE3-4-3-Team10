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

    @PostMapping("/add")
    fun addFriend(@RequestParam userId1: Long, @RequestParam userId2: Long): ResponseEntity<String> {
        friendshipService.addFriend(userId1, userId2)
        return ResponseEntity.ok("Friendship added successfully!")
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
        return ResponseEntity.ok("Friendship removed successfully!")
    }
}
