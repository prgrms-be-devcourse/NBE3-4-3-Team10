package com.ll.TeamProject.domain.friend.controller

import com.ll.TeamProject.domain.friend.service.FriendshipService
import com.ll.TeamProject.domain.user.entity.SiteUser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class FriendshipControllerTest {

 @Mock
 private lateinit var friendshipService: FriendshipService

 @InjectMocks
 private lateinit var friendshipController: FriendshipController

 @Test
 fun `친구 추가 - 성공`() {
  val user = SiteUser(id = 1L, username = "user1")
  val friendId = 2L

  Mockito.`when`(friendshipService.addFriend(user.id!!, friendId)).thenReturn(true)

  val response = friendshipController.addFriend(user, friendId)
  assertEquals(ResponseEntity.ok("친구가 추가되었습니다!"), response)
 }

 @Test
 fun `친구 추가 - 이미 친구일 때 실패`() {
  val user = SiteUser(id = 1L, username = "user1")
  val friendId = 2L

  Mockito.`when`(friendshipService.addFriend(user.id!!, friendId)).thenReturn(false)

  val response = friendshipController.addFriend(user, friendId)
  assertEquals(ResponseEntity.badRequest().body("이미 친구입니다!"), response)
 }

 @Test
 fun `친구 추가 - 자기 자신 추가 시 실패`() {
  val user = SiteUser(id = 1L, username = "user1")

  val response = friendshipController.addFriend(user, user.id!!)
  assertEquals(ResponseEntity.badRequest().body("자기 자신과는 이미 친구입니다!"), response)
 }

 @Test
 fun `친구 목록 조회 - 성공`() {
  val user = SiteUser(id = 1L, username = "user1")

  Mockito.`when`(friendshipService.getFriends(user.id!!)).thenReturn(emptyList())

  val response = friendshipController.getFriends(user)
  assertEquals(200, response.statusCode.value())
 }

 @Test
 fun `친구 삭제 - 성공`() {
  val user = SiteUser(id = 1L, username = "user1")
  val friendId = 2L

  Mockito.`when`(friendshipService.removeFriend(user.id!!, friendId)).thenReturn(true)

  val response = friendshipController.removeFriend(user, friendId)
  assertEquals(ResponseEntity.ok("친구가 삭제되었습니다!"), response)
 }

 @Test
 fun `친구 삭제 - 친구가 아닐 때 실패`() {
  val user = SiteUser(id = 1L, username = "user1")
  val friendId = 2L

  Mockito.`when`(friendshipService.removeFriend(user.id!!, friendId)).thenReturn(false)

  val response = friendshipController.removeFriend(user, friendId)
  assertEquals(ResponseEntity.badRequest().body("친구 관계가 존재하지 않습니다!"), response)
 }

 @Test
 fun `친구 삭제 - 존재하지 않는 사용자 삭제 시 실패`() {
  val user = SiteUser(id = 1L, username = "user1")
  val nonExistentFriendId = 999L

  Mockito.`when`(friendshipService.removeFriend(user.id!!, nonExistentFriendId)).thenReturn(false)

  val response = friendshipController.removeFriend(user, nonExistentFriendId)
  assertEquals(ResponseEntity.badRequest().body("친구 관계가 존재하지 않습니다!"), response)
 }
}
