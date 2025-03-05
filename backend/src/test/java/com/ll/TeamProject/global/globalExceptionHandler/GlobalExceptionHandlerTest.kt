package com.ll.TeamProject.global.globalExceptionHandler;

import com.ll.TeamProject.domain.user.exceptions.UserErrorCode;
import com.ll.TeamProject.global.exceptions.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    @DisplayName("404 - NoSuchElementException 예외 처리 테스트")
    void noSuchElementException() {
        NoSuchElementException exception = new NoSuchElementException("해당 데이터가 존재하지 않습니다.");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handle(exception);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("404", response.getBody().getErrorCode());
        assertEquals("해당 데이터가 존재하지 않습니다.", response.getBody().getMsg());
    }

    @Test
    @DisplayName("400 - 유효성 검증 실패 예외 처리 테스트")
    void validationException() {
        // 가짜 필드 에러 생성
        FieldError fieldError = new FieldError("user", "username", "필수 입력값입니다.");
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("400-VALIDATION", response.getBody().getErrorCode());
        assertEquals("username 필수 입력값입니다.", response.getBody().getMsg());
    }


    @Test
    @DisplayName("400 - 데이터 타입 오류 예외 처리 테스트")
    void typeMismatchException() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getMessage()).thenReturn("잘못된 데이터 타입입니다.");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTypeMismatchException(exception);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("400-TYPE", response.getBody().getErrorCode());
        assertEquals("잘못된 데이터 타입입니다.", response.getBody().getMsg());
    }

    @Test
    @DisplayName("403 - 권한 없음 예외 처리 테스트")
    void accessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("권한이 없습니다.");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(exception);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("403-FORBIDDEN", response.getBody().getErrorCode());
        assertEquals("권한이 없습니다.", response.getBody().getMsg());
    }

    @Test
    @DisplayName("405 - 지원하지 않는 HTTP 메서드 예외 처리 테스트")
    void methodNotAllowed() {
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("PUT");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodNotAllowed(exception);

        assertEquals(405, response.getStatusCodeValue());
        assertEquals("405", response.getBody().getErrorCode());
        assertEquals("지원하지 않는 HTTP 메서드입니다. 사용 가능한 메서드: 알 수 없음", response.getBody().getMsg());
    }

    @Test
    @DisplayName("500 - 서버 내부 오류 예외 처리 테스트")
    void genericException() {
        Exception exception = new Exception("서버 내부 오류 발생");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("500", response.getBody().getErrorCode());
        assertEquals("서버 내부 오류가 발생했습니다.", response.getBody().getMsg());
    }

    @Test
    @DisplayName("CustomException 예외 처리 테스트")
    void customException() {
        CustomException exception = new CustomException(UserErrorCode.INVALID_CREDENTIALS);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCustomException(exception);

        assertEquals(UserErrorCode.INVALID_CREDENTIALS.getStatus().value(), response.getStatusCodeValue());
        assertEquals(UserErrorCode.INVALID_CREDENTIALS.getCode(), response.getBody().getErrorCode());
        assertEquals(UserErrorCode.INVALID_CREDENTIALS.getMessage(), response.getBody().getMsg());
    }
}

