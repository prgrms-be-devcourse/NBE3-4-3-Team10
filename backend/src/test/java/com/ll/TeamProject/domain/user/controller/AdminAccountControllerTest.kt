package com.ll.TeamProject.domain.user.controller;

import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class AdminAccountControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserService userService;

    @Test
    @DisplayName("관리자 잠금 해제")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void unlockAdmin() throws Exception {
        SiteUser admin1 = userService.findByUsername("admin1").get();
        admin1.lockAccount();

        ResultActions resultActions = mvc
                .perform(
                        patch("/api/admin/%d/unlock".formatted(admin1.getId()))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(AdminAccountController.class))
                .andExpect(handler().methodName("unlockAdmin"))
                .andExpect(status().isNoContent());

        assertThat(admin1.isLocked()).isFalse();
    }
}