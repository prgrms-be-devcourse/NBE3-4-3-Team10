package com.ll.TeamProject.domain.calendar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.TeamProject.domain.calendar.dto.CalendarRequestDto;
import com.ll.TeamProject.domain.calendar.entity.Calendar;
import com.ll.TeamProject.domain.calendar.repository.CalendarRepository;
import com.ll.TeamProject.domain.user.TestUserHelper;
import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class CalendarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TestUserHelper testUserHelper;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;
    private String username;
    private Long calendarId;

    @BeforeEach
    void setUp() throws Exception {
        // 테스트 유저 생성
        username = "user3";
        SiteUser testUser = userService.findByUsername(username).orElseThrow();

        // 테스트 캘린더 생성
        Calendar testCalendar = Calendar.builder()
                .name("테스트 캘린더")
                .description("테스트 캘린더 설명")
                .user(testUser)
                .build();
        calendarRepository.save(testCalendar);
        calendarId = testCalendar.getId();
    }

    @Test
    @DisplayName("캘린더 생성 성공")
    void createCalendar_Success() throws Exception {
        // 요청 DTO 생성
        CalendarRequestDto requestDto = new CalendarRequestDto("새로운 캘린더", "새로운 캘린더 설명");
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // API 호출
        ResultActions resultActions = testUserHelper.requestWithUserAuth(
                username,
                post("/api/calendars")
                        .content(requestBody)
        ).andDo(print());

        // 응답 검증
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("새로운 캘린더"))
                .andExpect(jsonPath("$.description").value("새로운 캘린더 설명"));

        // DB 확인
        assertThat(calendarRepository.findByName("새로운 캘린더")).isPresent();
    }

    @Test
    @DisplayName("캘린더 조회 성공")
    void getCalendar_Success() throws Exception {
        // API 호출
        ResultActions resultActions = testUserHelper.requestWithUserAuth(
                username,
                get("/api/calendars/{id}", calendarId)
        ).andDo(print());

        // 응답 검증
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(calendarId))
                .andExpect(jsonPath("$.name").value("테스트 캘린더"))
                .andExpect(jsonPath("$.description").value("테스트 캘린더 설명"));
    }

    @Test
    @DisplayName("캘린더 조회 실패 - 존재하지 않는 캘린더")
    void getCalendar_NotFound() throws Exception {
        Long nonExistentId = calendarId + 100L; // 존재하지 않는 ID

        // API 호출
        ResultActions resultActions = testUserHelper.requestWithUserAuth(
                username,
                get("/api/calendars/{id}", nonExistentId)
        ).andDo(print());

        // 응답 검증
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value("캘린더를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("캘린더 수정 성공")
    void updateCalendar_Success() throws Exception {
        // 수정할 DTO 생성
        CalendarRequestDto updateDto = new CalendarRequestDto("수정된 캘린더", "수정된 설명");
        String requestBody = objectMapper.writeValueAsString(updateDto);

        // API 호출
        ResultActions resultActions = testUserHelper.requestWithUserAuth(
                username,
                put("/api/calendars/{id}", calendarId)
                        .content(requestBody)
        ).andDo(print());

        // 응답 검증
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("수정된 캘린더"))
                .andExpect(jsonPath("$.description").value("수정된 설명"));

        // DB 확인
        Calendar updatedCalendar = calendarRepository.findById(calendarId).orElseThrow();
        assertThat(updatedCalendar.getName()).isEqualTo("수정된 캘린더");
        assertThat(updatedCalendar.getDescription()).isEqualTo("수정된 설명");
    }

    @Test
    @DisplayName("캘린더 삭제 성공")
    void deleteCalendar_Success() throws Exception {
        // API 호출
        ResultActions resultActions = testUserHelper.requestWithUserAuth(
                username,
                delete("/api/calendars/{id}", calendarId)
        ).andDo(print());

        // 응답 검증
        resultActions.andExpect(status().isOk());

        // DB 확인
        assertThat(calendarRepository.findById(calendarId)).isEmpty();
    }

    @Test
    @DisplayName("캘린더 삭제 실패 - 존재하지 않는 캘린더")
    void deleteCalendar_NotFound() throws Exception {
        Long nonExistentId = calendarId + 100L; // 존재하지 않는 ID

        // API 호출
        ResultActions resultActions = testUserHelper.requestWithUserAuth(
                username,
                delete("/api/calendars/{id}", nonExistentId)
        ).andDo(print());

        // 응답 검증
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value("캘린더를 찾을 수 없습니다."));
    }
}
