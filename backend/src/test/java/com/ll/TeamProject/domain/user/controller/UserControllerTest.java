package com.ll.TeamProject.domain.user.controller;

import com.ll.TeamProject.domain.user.TestUserHelper;
import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private UserService userService;
    @Autowired
    private TestUserHelper testUserHelper;

    @Test
    @DisplayName("사용자 탈퇴")
    void deleteUser() throws Exception {
        String username = "user1";
        SiteUser actor = userService.findByUsername(username).get();
        MockHttpServletRequestBuilder request = delete("/api/user/%d".formatted(actor.getId()));

        ResultActions resultActions = testUserHelper.requestWithUserAuth(username, request);

        SiteUser deletedUser = userService.findById(actor.getId()).get();

        assertThat(deletedUser.getNickname()).startsWith("탈퇴한 사용자");
        assertThat(deletedUser.getCreateDate().toString()).startsWith(actor.getCreateDate().toString().substring(0, 25));
        assertThat(deletedUser.getModifyDate().toString()).startsWith(actor.getModifyDate().toString().substring(0, 25));

        resultActions
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("닉네임 변경, TestUserHelper 테스트")
    void changeNicknameAndUserHelperTest() throws Exception {
        String username = "user1";
        // 요청 생성
        MockHttpServletRequestBuilder request =
                post("/api/user")
                    .content("""
                            {
                                "nickname": "changedNickname"
                            }
                            """.stripIndent());

        // 인증 원하는 username 과 요청으로 도우미 메서드 호출
        ResultActions resultActions = testUserHelper.requestWithUserAuth(username, request);

        // 결과 확인
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.msg").value("사용자 정보가 수정되었습니다."));

        SiteUser actor = userService.findByUsername(username).get();
        assertThat(actor.getNickname()).isEqualTo("changedNickname");
    }

}