package com.ll.TeamProject.domain.user.controller;

import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.domain.user.enums.Role;
import com.ll.TeamProject.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class AdminUserControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserService userService;

    @Test
    @DisplayName("회원 목록 조회")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void userList() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/admin/users")
                                .param("page", "1")
                                .param("pageSize", "3")
                )
                .andDo(print());

        Page<SiteUser> userPage = userService.findUsers("", "", 1, 3, Role.USER);

        resultActions
                .andExpect(handler().handlerType(AdminUserController.class))
                .andExpect(handler().methodName("users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentPageNumber").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(3))
                .andExpect(jsonPath("$.data.totalPages").value(userPage.getTotalPages()))
                .andExpect(jsonPath("$.data.totalItems").value(userPage.getTotalElements()));

        List<SiteUser> users = userPage.getContent();

        for (int i = 0; i < users.size(); i++) {
            SiteUser user = users.get(i);
            resultActions
                    .andExpect(jsonPath("$.data.items[%d].id".formatted(i)).value(user.getId()))
                    .andExpect(jsonPath("$.data.items[%d].username".formatted(i)).value(user.getUsername()))
                    .andExpect(jsonPath("$.data.items[%d].email".formatted(i)).value(user.getEmail()))
                    .andExpect(jsonPath("$.data.items[%d].nickname".formatted(i)).value(user.getNickname()));
        }
    }

    @Test
    @DisplayName("회원 목록 조회 - 일반 회원 접근")
    @WithMockUser(username = "test", roles = "USER")
    void userList_NoAdmin() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/admin/users")
                )
                .andDo(print());

        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("USER_005"))
                .andExpect(jsonPath("$.msg").value("접근 권한이 없습니다."));
    }
}