package com.ll.TeamProject.domain.user;

import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.domain.user.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Component
public class TestUserHelper {

    private final UserService userService;
    private final MockMvc mvc;

    public TestUserHelper(UserService userService, MockMvc mvc) {
        this.userService = userService;
        this.mvc = mvc;
    }

    public ResultActions requestWithUserAuth(String username, MockHttpServletRequestBuilder request) throws Exception {
        SiteUser actor = userService.findByUsername(username).get();
        String actorAuthToken = userService.genAuthToken(actor);

        return mvc.perform(request
                .header("Authorization", "Bearer " + actorAuthToken)
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());
    }
}
