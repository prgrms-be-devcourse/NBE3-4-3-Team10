/*
package com.ll.TeamProject.domain.friend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ll.TeamProject.domain.friend.service.FriendshipService
import com.ll.TeamProject.domain.user.TestUserHelper
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.ResultActions
import org.springframework.transaction.annotation.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class FriendshipControllerTest {

 @Autowired
 private lateinit var friendshipService: FriendshipService

 @Autowired
 private lateinit var userService: UserService

 @Autowired
 private lateinit var testUserHelper: TestUserHelper

 @Autowired
 private lateinit var objectMapper: ObjectMapper

 private lateinit var user1: SiteUser
 private lateinit var user2: SiteUser

 @BeforeEach
 fun setUp() {
  user1 = userService.findByUsername("user1").orElseThrow()
  user2 = userService.findByUsername("user2").orElseThrow()
 }

 @Test
 @DisplayName("친구 추가 성공")
 fun addFriend_Success() {
  val resultActions: ResultActions = testUserHelper.requestWithUserAuth(
   user1.username,
   post("/api/friends/add")
    .param("userId1", user1.id.toString())
    .param("userId2", user2.id.toString())
  ).andDo(print())

  resultActions
   .andExpect(status().isOk)
   .andExpect(content().string(" 친구가 추가됬어요! "))

  val friends = friendshipService.getFriends(user1.id!!)
  assertThat(friends).contains(user2)
 }

 @Test
 @DisplayName("친구 추가 실패 - 이미 친구인 경우")
 fun addFriend_Fail_AlreadyFriends() {
  friendshipService.addFriend(user1.id!!, user2.id!!)

  val resultActions: ResultActions = testUserHelper.requestWithUserAuth(
   user1.username,
   post("/api/friends/add")
    .param("userId1", user1.id.toString())
    .param("userId2", user2.id.toString())
  ).andDo(print())

  resultActions
   .andExpect(status().isBadRequest)
   .andExpect(jsonPath("$.resultCode").value("400"))
   .andExpect(jsonPath("$.msg").value("이미 등록된 친구입니다!"))
 }

 @Test
 @DisplayName("친구 목록 조회 성공")
 fun getFriends_Success() {
  friendshipService.addFriend(user1.id!!, user2.id!!)

  val resultActions: ResultActions = testUserHelper.requestWithUserAuth(
   user1.username,
   get("/api/friends/{userId}", user1.id!!)
  ).andDo(print())

  resultActions
   .andExpect(status().isOk)
   .andExpect(jsonPath("$[0].id").value(user2.id!!))
 }

 @Test
 @DisplayName("친구 삭제 성공")
 fun removeFriend_Success() {
  friendshipService.addFriend(user1.id!!, user2.id!!)

  val resultActions: ResultActions = testUserHelper.requestWithUserAuth(
   user1.username,
   delete("/api/friends/remove")
    .param("userId1", user1.id.toString())
    .param("userId2", user2.id.toString())
  ).andDo(print())

  resultActions
   .andExpect(status().isOk)
   .andExpect(content().string(" 친구가 삭제됬어요! "))

  val friends = friendshipService.getFriends(user1.id!!)
  assertThat(friends).doesNotContain(user2)
 }
}
*/
