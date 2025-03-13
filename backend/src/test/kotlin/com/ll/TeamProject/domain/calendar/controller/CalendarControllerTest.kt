package com.ll.TeamProject.domain.calendar.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ll.TeamProject.domain.calendar.dto.CalendarRequestDto
import com.ll.TeamProject.domain.calendar.entity.Calendar
import com.ll.TeamProject.domain.calendar.repository.CalendarRepository
import com.ll.TeamProject.domain.user.TestUserHelper
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.transaction.annotation.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CalendarControllerTest {

    @Autowired
    private lateinit var calendarRepository: CalendarRepository

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var testUserHelper: TestUserHelper

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var username: String
    private var calendarId: Long = 0L

    @BeforeEach
    fun setUp() {
        username = "user3"
        val testUser: SiteUser = userService.findByUsername(username).orElseThrow()

        val testCalendar = Calendar(
            name = "테스트 캘린더",
            description = "테스트 캘린더 설명",
            user = testUser
        )
        calendarRepository.save(testCalendar)
        calendarId = testCalendar.id!!
    }

    @Test
    @DisplayName("캘린더 생성 성공")
    fun createCalendar_Success() {
        val requestDto = CalendarRequestDto("새로운 캘린더", "새로운 캘린더 설명")
        val requestBody = objectMapper.writeValueAsString(requestDto)

        val resultActions: ResultActions = testUserHelper.requestWithUserAuth(
            username,
            post("/api/calendars").content(requestBody)
        ).andDo(print())

        resultActions
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("새로운 캘린더"))
            .andExpect(jsonPath("$.description").value("새로운 캘린더 설명"))

        assertThat(calendarRepository.findByName("새로운 캘린더")).isPresent
    }

    @Test
    @DisplayName("캘린더 조회 성공")
    fun getCalendar_Success() {
        val resultActions: ResultActions = testUserHelper.requestWithUserAuth(
            username,
            get("/api/calendars/{id}", calendarId)
        ).andDo(print())

        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(calendarId))
            .andExpect(jsonPath("$.name").value("테스트 캘린더"))
            .andExpect(jsonPath("$.description").value("테스트 캘린더 설명"))
    }

    @Test
    @DisplayName("캘린더 조회 실패 - 존재하지 않는 캘린더")
    fun getCalendar_NotFound() {
        val nonExistentId = calendarId + 100L

        val resultActions: ResultActions = testUserHelper.requestWithUserAuth(
            username,
            get("/api/calendars/{id}", nonExistentId)
        ).andDo(print())

        resultActions
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.msg").value("캘린더를 찾을 수 없습니다."))
    }

    @Test
    @DisplayName("캘린더 수정 성공")
    fun updateCalendar_Success() {
        val updateDto = CalendarRequestDto("수정된 캘린더", "수정된 설명")
        val requestBody = objectMapper.writeValueAsString(updateDto)

        val resultActions: ResultActions = testUserHelper.requestWithUserAuth(
            username,
            put("/api/calendars/{id}", calendarId).content(requestBody)
        ).andDo(print())

        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("수정된 캘린더"))
            .andExpect(jsonPath("$.description").value("수정된 설명"))

        val updatedCalendar = calendarRepository.findById(calendarId).orElseThrow()
        assertThat(updatedCalendar.name).isEqualTo("수정된 캘린더")
        assertThat(updatedCalendar.description).isEqualTo("수정된 설명")
    }

    @Test
    @DisplayName("캘린더 삭제 성공")
    fun deleteCalendar_Success() {
        val resultActions: ResultActions = testUserHelper.requestWithUserAuth(
            username,
            delete("/api/calendars/{id}", calendarId)
        ).andDo(print())

        resultActions.andExpect(status().isOk)
        assertThat(calendarRepository.findById(calendarId)).isEmpty
    }

    @Test
    @DisplayName("캘린더 삭제 실패 - 존재하지 않는 캘린더")
    fun deleteCalendar_NotFound() {
        val nonExistentId = calendarId + 100L

        val resultActions: ResultActions = testUserHelper.requestWithUserAuth(
            username,
            delete("/api/calendars/{id}", nonExistentId)
        ).andDo(print())

        resultActions
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.msg").value("캘린더를 찾을 수 없습니다."))
    }
}