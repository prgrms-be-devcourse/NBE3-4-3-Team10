package com.ll.TeamProject.domain.user.controller

import com.ll.TeamProject.domain.user.service.UserService
import org.assertj.core.api.Assertions.assertThat
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
internal class AdminAccountControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var userService: UserService

    @Test
    @DisplayName("관리자 잠금 해제")
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun unlockAdmin() {
        val admin1 = userService.findByUsername("admin1").get()
        admin1.lockAccount()
        assertThat(admin1.isLocked()).isTrue()

        val resultActions = mvc.perform(
                MockMvcRequestBuilders.patch("/api/admin/${admin1.id}/unlock")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AdminAccountController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("unlockAdmin"))
            .andExpect(MockMvcResultMatchers.status().isNoContent())

        assertThat(admin1.isLocked()).isFalse()
    }
}