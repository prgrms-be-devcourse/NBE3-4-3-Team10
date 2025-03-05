package com.ll.TeamProject.global.rsData;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Getter
public class ResponseDto<T> {
    private final boolean success;   // 성공 여부를 명시적으로 표현
    private final String code;       // 내부 에러 코드 또는 성공 코드 (예: "SUCCESS", "USER_001" 등)
    private final String msg;    // 응답 메시지
    private final T data;            // 실제 데이터 (성공 응답 시)

    // 성공 응답 생성
    public static <T> ResponseDto<T> success(String msg, T data) {
        return new ResponseDto<>(true, "SUCCESS", msg, data);
    }

    public static <T> ResponseDto<T> success(String msg) {
        return new ResponseDto<>(true, "SUCCESS", msg, null);
    }

    // 실패 응답 생성 (비즈니스 로직에서 처리하는 경우, 단 ResponseDto 성공 응답에 주로 사용)
    public static <T> ResponseDto<T> failure(String code, String msg) {
        return new ResponseDto<>(false, code, msg, null);
    }
}