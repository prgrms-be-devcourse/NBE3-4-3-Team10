package com.ll.TeamProject.domain.user

import com.ll.TeamProject.domain.user.service.UserService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import java.nio.charset.StandardCharsets

@Component
@ConditionalOnProperty(name = ["spring.profiles.active"], havingValue = "test")
class TestUserHelper(
    private val userService: UserService,
    private val mvc: MockMvc
) {
    fun requestWithUserAuth(username: String, request: MockHttpServletRequestBuilder): ResultActions {
        val actor = userService.findByUsername(username).get()
        val actorAuthToken = userService.genAuthToken(actor)

        return mvc.perform(
            request
                .header("Authorization", "Bearer $actorAuthToken")
                .contentType(MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(MockMvcResultHandlers.print())
    }
}
