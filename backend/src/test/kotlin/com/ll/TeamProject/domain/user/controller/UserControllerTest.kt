package com.ll.TeamProject.domain.user.controller

import com.ll.TeamProject.domain.user.TestUserHelper
import com.ll.TeamProject.domain.user.service.UserService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
internal class UserControllerTest {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var testUserHelper: TestUserHelper

    @Test
    @DisplayName("사용자 탈퇴")
    fun deleteUser() {
        val username = "user1"
        val actor = userService.findByUsername(username).get()
        val request = MockMvcRequestBuilders.delete("/api/user/${actor.id}")

        val resultActions = testUserHelper.requestWithUserAuth(username, request)

        val deletedUser = userService.findById(actor.id!!).get()

        Assertions.assertThat(deletedUser.nickname).startsWith("탈퇴한 사용자")
        Assertions.assertThat(deletedUser.createDate.toString())
            .startsWith(actor.createDate.toString().substring(0, 25))
        Assertions.assertThat(deletedUser.modifyDate.toString())
            .startsWith(actor.modifyDate.toString().substring(0, 25))

        resultActions
            .andExpect(MockMvcResultMatchers.status().isNoContent())
    }

    @Test
    @DisplayName("닉네임 변경, TestUserHelper 테스트")
    fun changeNicknameAndUserHelperTest() {
        val username = "user1"
        // 요청 생성
        val request =
            MockMvcRequestBuilders.post("/api/user")
                .content("""
                    {
                        "nickname": "changedNickname"
                    }
                    """.trimIndent())

        // 인증 원하는 username 과 요청으로 도우미 메서드 호출
        val resultActions = testUserHelper.requestWithUserAuth(username, request)

        // 결과 확인
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("SUCCESS"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("사용자 정보가 수정되었습니다."))

        val actor = userService.findByUsername(username).get()
        Assertions.assertThat(actor.nickname).isEqualTo("changedNickname")
    }
}