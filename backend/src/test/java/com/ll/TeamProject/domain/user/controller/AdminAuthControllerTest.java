package com.ll.TeamProject.domain.user.controller;

import com.ll.TeamProject.domain.user.TestUserHelper;
import com.ll.TeamProject.domain.user.entity.Authentication;
import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.domain.user.enums.AuthType;
import com.ll.TeamProject.domain.user.service.AuthenticationService;
import com.ll.TeamProject.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class AdminAuthControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserService userService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    private TestUserHelper testUserHelper;

    @Test
    @DisplayName("관리자 로그인")
    void adminLogin() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/admin/login")
                                .content("""
                                            {
                                                "username": "admin1",
                                                "password": "admin1"
                                            }
                                            """.stripIndent()
                                )
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        SiteUser actor = userService.findByUsername("admin1").get();
        Authentication authentication = authenticationService.findByUserId(actor.getId()).get();

        assertThat(authentication).isNotNull();
        assertThat(authentication.getUser()).isEqualTo(actor);
        assertThat(authentication.getAuthType()).isEqualTo(AuthType.LOCAL);

        resultActions
                .andExpect(handler().handlerType(AdminAuthController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.msg").value("%s님 환영합니다.".formatted(actor.getNickname())))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.item").exists())
                .andExpect(jsonPath("$.data.item.id").value(actor.getId()))
                .andExpect(jsonPath("$.data.item.createDate").value(Matchers.startsWith(actor.getCreateDate().toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.item.modifyDate").value(Matchers.startsWith(actor.getModifyDate().toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.item.username").value(actor.getUsername()))
                .andExpect(jsonPath("$.data.item.nickname").value(actor.getNickname()))
                .andExpect(jsonPath("$.data.apiKey").value(actor.getApiKey()))
                .andExpect(jsonPath("$.data.accessToken").exists());

        resultActions.andExpect(
                result -> {
                    Cookie accessTokenCookie = result.getResponse().getCookie("accessToken");
                    assertThat(accessTokenCookie.getValue()).isNotBlank();
                    assertThat(accessTokenCookie.getPath()).isEqualTo("/");
                    assertThat(accessTokenCookie.isHttpOnly()).isTrue();

                    Cookie apiKeyCookie = result.getResponse().getCookie("apiKey");
                    assertThat(apiKeyCookie.getValue()).isEqualTo(actor.getApiKey());
                    assertThat(apiKeyCookie.getPath()).isEqualTo("/");
                    assertThat(apiKeyCookie.isHttpOnly()).isTrue();
                }
        );
    }

    @Test
    @DisplayName("로그아웃")
    void logout() throws Exception {
        MockHttpServletRequestBuilder request = post("/api/admin/logout");

        ResultActions resultActions = testUserHelper.requestWithUserAuth("user1", request);

        resultActions
                .andExpect(status().isNoContent())
                .andExpect(result -> {
                    Cookie accessTokenCookie = result.getResponse().getCookie("accessToken");
                    assertThat(accessTokenCookie.getValue()).isEmpty();
                    assertThat(accessTokenCookie.getMaxAge()).isEqualTo(0);
                    assertThat(accessTokenCookie.getPath()).isEqualTo("/");
                    assertThat(accessTokenCookie.isHttpOnly()).isTrue();

                    Cookie apiKeyCookie = result.getResponse().getCookie("apiKey");
                    assertThat(apiKeyCookie.getValue()).isEmpty();
                    assertThat(apiKeyCookie.getMaxAge()).isEqualTo(0);
                    assertThat(apiKeyCookie.getPath()).isEqualTo("/");
                    assertThat(apiKeyCookie.isHttpOnly()).isTrue();
                });
    }

}