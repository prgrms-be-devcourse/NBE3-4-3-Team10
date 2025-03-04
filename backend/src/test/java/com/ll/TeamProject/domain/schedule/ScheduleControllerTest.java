package com.ll.TeamProject.domain.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ll.TeamProject.domain.calendar.repository.CalendarRepository;
import com.ll.TeamProject.domain.schedule.dto.ScheduleRequestDto;
import com.ll.TeamProject.domain.schedule.entity.Schedule;
import com.ll.TeamProject.domain.schedule.repository.ScheduleRepository;
import com.ll.TeamProject.domain.user.TestUserHelper;
import com.ll.TeamProject.global.jpa.entity.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ScheduleControllerTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private TestUserHelper testUserHelper;

    // 날짜/시간 관련 상수
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // 테스트 데이터
    private final LocalDateTime tomorrow = LocalDateTime.now().plusHours(24);
    private LocalDateTime startTime1;
    private LocalDateTime endTime1;
    private LocalDateTime startTime2;
    private LocalDateTime endTime2;
    private String formattedStartTime1;
    private String formattedEndTime1;
    private String formattedStartTime2;
    private String formattedEndTime2;

    // API 경로 상수
    private static final String SCHEDULES_API_PATH = "/api/calendars/{calendarId}/schedules";
    private static final String SCHEDULE_API_PATH = "/api/calendars/{calendarId}/schedules/{scheduleId}";

    // 테스트 상태 변수
    private Long calendarId;
    private Long scheduleId1;
    private Long scheduleId2;
    private String username;

    // 테스트 데이터 생성용 객체
    private ScheduleRequestDto meetingScheduleDto;
    private ScheduleRequestDto workoutScheduleDto;

    @BeforeEach
    void setUp() {
        // ObjectMapper 설정
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 테스트 데이터 초기화
        scheduleRepository.deleteAll();

        // 날짜/시간 설정
        startTime1 = tomorrow.withHour(10).withMinute(0).withSecond(0).withNano(0);
        endTime1 = startTime1.plusHours(1);
        startTime2 = tomorrow.withHour(12).withMinute(0).withSecond(0).withNano(0);
        endTime2 = startTime2.plusHours(1);

        // 포맷팅된 날짜/시간 문자열
        formattedStartTime1 = startTime1.format(FORMATTER);
        formattedEndTime1 = endTime1.format(FORMATTER);
        formattedStartTime2 = startTime2.format(FORMATTER);
        formattedEndTime2 = endTime2.format(FORMATTER);

        // 캘린더 ID 설정
        calendarId = 1L;

        // 테스트용 DTOs 생성
        meetingScheduleDto = new ScheduleRequestDto(
                "회의 일정",
                "팀 회의",
                startTime1,
                endTime1,
                new Location(37.5665, 126.9780, "서울특별시 중구 세종대로 110")
        );

        workoutScheduleDto = new ScheduleRequestDto(
                "운동 일정",
                "헬스장 방문",
                startTime2,
                endTime2,
                new Location(37.5678, 126.9890, "서울특별시 강남구 테헤란로 123")
        );
    }


    @BeforeEach
    void setUpTestData() throws Exception {
        // 사용자 정보 조회
        username = calendarRepository.findUsernameByCalendarId(calendarId);

        // 두 개의 일정 생성 (회의 일정, 운동 일정)
        scheduleId1 = createSchedule(meetingScheduleDto);
        scheduleId2 = createSchedule(workoutScheduleDto);
    }


    private Long createSchedule(ScheduleRequestDto dto) throws Exception {
        String requestBody = OBJECT_MAPPER.writeValueAsString(dto);

        String responseJson = testUserHelper.requestWithUserAuth(
                        username,
                        post(SCHEDULES_API_PATH, calendarId)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        return OBJECT_MAPPER.readTree(responseJson).get("id").asLong();
    }


    private void assertScheduleResponse(ResultActions resultActions,
                                        ScheduleRequestDto dto,
                                        String startTime,
                                        String endTime) throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(dto.title()))
                .andExpect(jsonPath("$.description").value(dto.description()))
                .andExpect(jsonPath("$.startTime").value(startTime))
                .andExpect(jsonPath("$.endTime").value(endTime))
                .andExpect(jsonPath("$.location.latitude").value(dto.location().getLatitude()))
                .andExpect(jsonPath("$.location.longitude").value(dto.location().getLongitude()))
                .andExpect(jsonPath("$.location.address").value(dto.location().getAddress()));
    }



    @Test
    @DisplayName("일정 생성 성공")
    void t1() throws Exception {
        scheduleRepository.deleteAll();

        // 첫 번째 일정 생성 테스트
        String requestBody1 = OBJECT_MAPPER.writeValueAsString(meetingScheduleDto);
        ResultActions resultActions1 = testUserHelper.requestWithUserAuth(
                        username,
                        post(SCHEDULES_API_PATH, calendarId)
                                .content(requestBody1)
                )
                .andDo(print());

        // 두 번째 일정 생성 테스트
        String requestBody2 = OBJECT_MAPPER.writeValueAsString(workoutScheduleDto);
        ResultActions resultActions2 = testUserHelper.requestWithUserAuth(
                        username,
                        post(SCHEDULES_API_PATH, calendarId)
                                .content(requestBody2)
                )
                .andDo(print());

        // 응답 검증
        assertScheduleResponse(resultActions1, meetingScheduleDto, formattedStartTime1, formattedEndTime1);
        assertScheduleResponse(resultActions2, workoutScheduleDto, formattedStartTime2, formattedEndTime2);
    }


//    @Test
//    @DisplayName("일정 생성 실패 - 중복 시간")
//    void t2() throws Exception {
//        Long calendarId = 1L;
//
//        LocalDateTime startTime = startTime1; // 기존 일정과 동일한 시작 시간
//        LocalDateTime endTime = endTime1; // 기존 일정과 동일한 종료 시간
//
//        // 중복 시간 요청 데이터
//        ScheduleRequestDto dto = new ScheduleRequestDto("중복 시간 테스트",
//                "겹치는 일정",
//                startTime,
//                endTime,
//                new Location(37.5665, 126.9780, "서울특별시 중구 세종대로 110")
//        );
//
//        String requestBody = objectMapper.writeValueAsString(dto);
//
//        // Perform POST 요청
//        ResultActions resultActions = testUserHelper.requestWithUserAuth(username,
//                        post("/api/calendars/{calendarId}/schedules", calendarId)
//                                .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andDo(print());
//
//        // 기대 응답 검증
//        resultActions
//                .andExpect(status().isBadRequest()) // 400 응답 확인
//                .andExpect(jsonPath("$.msg").value("해당 시간에 이미 일정이 존재합니다.")); // 예외 메시지 확인
//    }



    @Test
    @DisplayName("일정 목록 조회 - 성공")
    void t3() throws Exception {
// 특정 날짜의 일정 목록 조회
        ResultActions resultActions = testUserHelper.requestWithUserAuth(
                        username,
                        get(SCHEDULES_API_PATH, calendarId)
                                .param("startDate", tomorrow.toLocalDate().toString())
                                .param("endDate", tomorrow.toLocalDate().toString())
                )
                .andDo(print());

        // 응답 검증
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                // 첫 번째 일정 검증 (회의 일정)
                .andExpect(jsonPath("$[0].title").value(meetingScheduleDto.title()))
                .andExpect(jsonPath("$[0].description").value(meetingScheduleDto.description()))
                .andExpect(jsonPath("$[0].startTime").value(formattedStartTime1))
                .andExpect(jsonPath("$[0].endTime").value(formattedEndTime1))
                .andExpect(jsonPath("$[0].location.latitude").value(meetingScheduleDto.location().getLatitude()))
                .andExpect(jsonPath("$[0].location.longitude").value(meetingScheduleDto.location().getLongitude()))
                .andExpect(jsonPath("$[0].location.address").value(meetingScheduleDto.location().getAddress()))
                // 두 번째 일정 검증 (운동 일정)
                .andExpect(jsonPath("$[1].title").value(workoutScheduleDto.title()))
                .andExpect(jsonPath("$[1].description").value(workoutScheduleDto.description()))
                .andExpect(jsonPath("$[1].startTime").value(formattedStartTime2))
                .andExpect(jsonPath("$[1].endTime").value(formattedEndTime2))
                .andExpect(jsonPath("$[1].location.latitude").value(workoutScheduleDto.location().getLatitude()))
                .andExpect(jsonPath("$[1].location.longitude").value(workoutScheduleDto.location().getLongitude()))
                .andExpect(jsonPath("$[1].location.address").value(workoutScheduleDto.location().getAddress()));
    }



    @Test
    @DisplayName("일정 목록 조회 - 일정 없음")
    void t4() throws Exception {
        // 일정이 없는 기간 조회
        ResultActions resultActions = testUserHelper.requestWithUserAuth(
                        username,
                        get(SCHEDULES_API_PATH, calendarId)
                                .param("startDate", "2024-03-01")
                                .param("endDate", "2024-03-02")
                )
                .andDo(print());

        // 응답 검증 (빈 배열)
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("특정 일정 조회 성공")
    void t5() throws Exception {
        // 특정 일정 조회
        ResultActions resultActions = testUserHelper.requestWithUserAuth(
                        username,
                        get(SCHEDULE_API_PATH, calendarId, scheduleId1)
                )
                .andDo(print());

        // 응답 검증
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(scheduleId1))
                .andExpect(jsonPath("$.title").value(meetingScheduleDto.title()))
                .andExpect(jsonPath("$.description").value(meetingScheduleDto.description()))
                .andExpect(jsonPath("$.startTime").value(formattedStartTime1))
                .andExpect(jsonPath("$.endTime").value(formattedEndTime1))
                .andExpect(jsonPath("$.location.latitude").value(meetingScheduleDto.location().getLatitude()))
                .andExpect(jsonPath("$.location.longitude").value(meetingScheduleDto.location().getLongitude()))
                .andExpect(jsonPath("$.location.address").value(meetingScheduleDto.location().getAddress()));
    }

    @Test
    @DisplayName("특정 일정 조회 실패 - 일정이 존재하지 않음")
    void t6() throws Exception {
        // 존재하지 않는 일정 ID 계산
        Long nonExistentScheduleId = scheduleRepository.findTopByOrderByIdDesc()
                .map(Schedule::getId)
                .orElse(0L) + 1;

        // 존재하지 않는 일정 조회
        ResultActions resultActions = testUserHelper.requestWithUserAuth(
                        username,
                        get(SCHEDULE_API_PATH, calendarId, nonExistentScheduleId)
                )
                .andDo(print());

        // 응답 검증 (404 Not Found)
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value("해당 일정을 찾을 수 없습니다."));
    }



    @Test
    @DisplayName("일정 수정 성공")
    void t7() throws Exception {
        // 수정할 일정 정보 준비
        LocalDateTime newStartTime = tomorrow.withHour(17).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime newEndTime = newStartTime.plusHours(3);
        String formattedNewStartTime = newStartTime.format(FORMATTER);
        String formattedNewEndTime = newEndTime.format(FORMATTER);

        // 수정 요청 DTO 생성
        ScheduleRequestDto updateDto = new ScheduleRequestDto(
                "수정된 일정 제목",
                "수정된 일정 설명",
                newStartTime,
                newEndTime,
                new Location(37.5678, 126.9890, "서울특별시 중구 세종대로 110")
        );

        String requestBody = OBJECT_MAPPER.writeValueAsString(updateDto);

        // 일정 수정 요청
        ResultActions resultActions = testUserHelper.requestWithUserAuth(
                        username,
                        put(SCHEDULE_API_PATH, calendarId, scheduleId1)
                                .content(requestBody)
                )
                .andDo(print());

        // 응답 검증
        assertScheduleResponse(resultActions, updateDto, formattedNewStartTime, formattedNewEndTime);
    }


    @Test
    @DisplayName("일정 수정 실패 - 일정이 존재하지 않음")
    void t8() throws Exception {
        // 존재하지 않는 일정 ID 계산
        Long nonExistentScheduleId = scheduleRepository.findTopByOrderByIdDesc()
                .map(Schedule::getId)
                .orElse(0L) + 1;

        // 수정 요청 DTO 생성
        LocalDateTime newStartTime = tomorrow.withHour(17).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime newEndTime = newStartTime.plusHours(3);

        ScheduleRequestDto updateDto = new ScheduleRequestDto(
                "수정된 일정 제목",
                "수정된 일정 설명",
                newStartTime,
                newEndTime,
                new Location(37.5678, 126.9890, "서울특별시 중구 세종대로 110")
        );

        String requestBody = OBJECT_MAPPER.writeValueAsString(updateDto);

        // 존재하지 않는 일정 수정 요청
        ResultActions resultActions = testUserHelper.requestWithUserAuth(
                        username,
                        put(SCHEDULE_API_PATH, calendarId, nonExistentScheduleId)
                                .content(requestBody)
                )
                .andDo(print());

        // 응답 검증 (404 Not Found)
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value("해당 일정을 찾을 수 없습니다."));
    }


//    @Test
//    @DisplayName("일정 수정 실패 - 시간 충돌")
//    void t9() throws Exception {
//        Long calendarId = 1L;
//        Long scheduleId = scheduleId1;
//
//        // 기존 일정과 동일한 시간으로 업데이트하여 시간 충돌 발생 유도
//        LocalDateTime startTime = startTime2; // scheduleId=2 일정과 동일한 시작 시간
//        LocalDateTime endTime = endTime2; // scheduleId=2 일정과 동일한 종료 시간
//
//        // 수정 요청 데이터
//        ScheduleRequestDto updateDto = new ScheduleRequestDto(
//                "겹치는 시간 수정 요청",
//                "겹치는 일정 테스트",
//                startTime,
//                endTime,
//                new Location(37.5678, 126.9890, "서울특별시 중구 세종대로 110")
//        );
//
//        String requestBody = objectMapper.writeValueAsString(updateDto);
//
//        // Perform PUT 요청 (시간 충돌 발생 예상)
//        ResultActions resultActions = testUserHelper.requestWithUserAuth(username,
//                put("/api/calendars/{calendarId}/schedules/{scheduleId}", calendarId, scheduleId)
//                        .content(requestBody)
//        ).andDo(print());
//
//        // 기대 응답 검증
//        resultActions
//                .andExpect(status().isBadRequest()) // 400 응답 확인
//                .andExpect(jsonPath("$.msg").value("해당 시간에 이미 일정이 존재합니다.")); // 예외 메시지 확인
//    }


    @Test
    @DisplayName("일정 삭제 성공")
    void t10() throws Exception {
        // 일정 삭제 요청
        ResultActions resultActions = testUserHelper.requestWithUserAuth(
                        username,
                        delete(SCHEDULE_API_PATH, calendarId, scheduleId1)
                )
                .andDo(print());

        // 응답 검증 (204 No Content)
        resultActions.andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("일정 삭제 실패 - 일정이 존재하지 않음")
    void t11() throws Exception {
        // 존재하지 않는 일정 ID 계산
        Long nonExistentScheduleId = scheduleRepository.findTopByOrderByIdDesc()
                .map(Schedule::getId)
                .orElse(0L) + 1;

        // 존재하지 않는 일정 삭제 요청
        ResultActions resultActions = testUserHelper.requestWithUserAuth(
                        username,
                        delete(SCHEDULE_API_PATH, calendarId, nonExistentScheduleId)
                )
                .andDo(print());

        // 응답 검증 (404 Not Found)
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value("해당 일정을 찾을 수 없습니다."));
    }
}