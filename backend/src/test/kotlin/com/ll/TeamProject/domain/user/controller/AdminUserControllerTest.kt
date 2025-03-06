package com.ll.TeamProject.domain.user.controller

import com.ll.TeamProject.domain.user.enums.Role
import com.ll.TeamProject.domain.user.service.UserService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
internal class AdminUserControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var userService: UserService

    @Test
    @DisplayName("회원 목록 조회")
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun userList() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/admin/users")
                    .param("page", "1")
                    .param("pageSize", "3")
            )
            .andDo(MockMvcResultHandlers.print())

        val userPage = userService.findUsers(
            "", "", 1, 3, Role.USER)

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AdminUserController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("users"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.currentPageNumber").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.pageSize").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalPages").value(userPage.totalPages))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalItems").value(userPage.totalElements))

        val users = userPage.content

        for (i in users.indices) {
            val user = users[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.items[$i].id").value(user.id))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.items[$i].username").value(user.username)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.items[$i].email").value(user.email))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.items[$i].nickname").value(user.nickname)
                )
        }
    }

    @Test
    @DisplayName("회원 목록 조회 - 일반 회원 접근")
    @WithMockUser(username = "test", roles = ["USER"])
    fun userList_NoAdmin() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/admin/users")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.status().isForbidden())
            .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value("USER_005"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("접근 권한이 없습니다."))
    }
}