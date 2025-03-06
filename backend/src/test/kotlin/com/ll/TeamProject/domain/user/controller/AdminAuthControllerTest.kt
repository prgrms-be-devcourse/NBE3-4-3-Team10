package com.ll.TeamProject.domain.user.controller

import com.ll.TeamProject.domain.user.TestUserHelper
import com.ll.TeamProject.domain.user.enums.AuthType
import com.ll.TeamProject.domain.user.service.AuthenticationService
import com.ll.TeamProject.domain.user.service.UserService
import org.assertj.core.api.Assertions
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
internal class AdminAuthControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var authenticationService: AuthenticationService

    @Autowired
    private lateinit var testUserHelper: TestUserHelper

    @Test
    @DisplayName("관리자 로그인")
    fun adminLogin() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/admin/login")
                    .content(
                        """
                                            {
                                                "username": "admin1",
                                                "password": "admin1"
                                            }
                                            
                                            """.trimIndent()
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        val actor = userService.findByUsername("admin1").get()
        val authentication = authenticationService.findByUserId(actor.id!!).get()

        Assertions.assertThat(authentication).isNotNull()
        Assertions.assertThat(authentication.user).isEqualTo(actor)
        Assertions.assertThat(authentication.authType).isEqualTo(AuthType.LOCAL)

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AdminAuthController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("login"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("SUCCESS"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${actor.nickname}님 환영합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.item").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.item.id").value(actor.id))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.item.createDate")
                    .value(Matchers.startsWith(actor.createDate.toString().substring(0, 25)))
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.item.modifyDate")
                    .value(Matchers.startsWith(actor.modifyDate.toString().substring(0, 25)))
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.item.username").value(actor.username))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.item.nickname").value(actor.nickname))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.apiKey").value(actor.apiKey))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").exists())

        resultActions.andExpect { result: MvcResult ->
            val accessTokenCookie = result.response.getCookie("accessToken")
            Assertions.assertThat(accessTokenCookie!!.value).isNotBlank()
            Assertions.assertThat(accessTokenCookie.path).isEqualTo("/")
            Assertions.assertThat(accessTokenCookie.isHttpOnly).isTrue()

            val apiKeyCookie = result.response.getCookie("apiKey")
            Assertions.assertThat(apiKeyCookie!!.value).isEqualTo(actor.apiKey)
            Assertions.assertThat(apiKeyCookie.path).isEqualTo("/")
            Assertions.assertThat(apiKeyCookie.isHttpOnly).isTrue()
        }
    }

    @Test
    @DisplayName("로그아웃")
    fun logout() {
        val request = MockMvcRequestBuilders.post("/api/admin/logout")

        val resultActions = testUserHelper.requestWithUserAuth("user1", request)

        resultActions
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andExpect { result: MvcResult ->
                val accessTokenCookie = result.response.getCookie("accessToken")
                Assertions.assertThat(accessTokenCookie!!.value).isEmpty()
                Assertions.assertThat(accessTokenCookie.maxAge).isEqualTo(0)
                Assertions.assertThat(accessTokenCookie.path).isEqualTo("/")
                Assertions.assertThat(accessTokenCookie.isHttpOnly).isTrue()

                val apiKeyCookie = result.response.getCookie("apiKey")
                Assertions.assertThat(apiKeyCookie!!.value).isEmpty()
                Assertions.assertThat(apiKeyCookie.maxAge).isEqualTo(0)
                Assertions.assertThat(apiKeyCookie.path).isEqualTo("/")
                Assertions.assertThat(apiKeyCookie.isHttpOnly).isTrue()
            }
    }
}