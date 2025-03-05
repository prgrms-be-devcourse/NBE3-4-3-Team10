package com.ll.TeamProject.global.globalExceptionHandler

import com.ll.TeamProject.domain.user.exceptions.UserErrorCode
import com.ll.TeamProject.global.exceptions.CustomException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.TypeMismatchException
import org.springframework.core.MethodParameter
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException

@ExtendWith(MockitoExtension::class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private lateinit var exceptionHandler: GlobalExceptionHandler

    @Test
    @DisplayName("404 - NoSuchElementException 예외 처리 테스트")
    fun noSuchElementException() {
        val exception = NoSuchElementException("해당 데이터가 존재하지 않습니다.")

        val response: ResponseEntity<ErrorResponse> = exceptionHandler.handle(exception)

        assertEquals(404, response.statusCode.value())
        assertEquals("404", response.body?.errorCode)
        assertEquals("해당 데이터가 존재하지 않습니다.", response.body?.msg)
    }

    @Test
    @DisplayName("400 - 유효성 검증 실패 예외 처리 테스트")
    fun validationException() {
        val fieldError = FieldError("user", "username", "필수 입력값입니다.")
        val bindingResult = mock(BindingResult::class.java)

        `when`(bindingResult.fieldErrors).thenReturn(listOf(fieldError))

        val methodParameter = mock(MethodParameter::class.java)

        val exception = MethodArgumentNotValidException(methodParameter, bindingResult)

        val response: ResponseEntity<ErrorResponse> = exceptionHandler.handleValidationException(exception)

        assertEquals(400, response.statusCode.value())
        assertEquals("400-VALIDATION", response.body?.errorCode)
        assertEquals("username 필수 입력값입니다.", response.body?.msg)
    }

    @Test
    @DisplayName("400 - 데이터 타입 오류 예외 처리 테스트")
    fun typeMismatchException() {
        val exception = mock(TypeMismatchException::class.java)
        `when`(exception.message).thenReturn("잘못된 데이터 타입입니다.")

        val response: ResponseEntity<ErrorResponse> = exceptionHandler.handleTypeMismatchException(exception)

        assertEquals(400, response.statusCode.value())
        assertEquals("400-TYPE", response.body?.errorCode)
        assertEquals("잘못된 데이터 타입입니다.", response.body?.msg)
    }

    @Test
    @DisplayName("400 - 필수 요청 파라미터 누락 예외 처리 테스트")
    fun missingParamException() {
        val exception = mock(MissingServletRequestParameterException::class.java)
        `when`(exception.message).thenReturn("필수 요청 파라미터가 누락되었습니다.")

        val response: ResponseEntity<ErrorResponse> = exceptionHandler.handleMissingParamException(exception)

        assertEquals(400, response.statusCode.value())
        assertEquals("400-MISSING_PARAM", response.body?.errorCode)
        assertEquals("필수 요청 파라미터가 누락되었습니다.", response.body?.msg)
    }

    @Test
    @DisplayName("400 - 요청 JSON 파싱 오류 예외 처리 테스트")
    fun jsonParsingException() {
        val exception = mock(HttpMessageNotReadableException::class.java)
        `when`(exception.message).thenReturn("잘못된 JSON 형식입니다.")

        val response: ResponseEntity<ErrorResponse> = exceptionHandler.handleJsonParsingException(exception)

        assertEquals(400, response.statusCode.value())
        assertEquals("400-JSON_PARSE", response.body?.errorCode)
        assertEquals("잘못된 JSON 형식입니다.", response.body?.msg)
    }

    @Test
    @DisplayName("403 - 권한 없음 예외 처리 테스트")
    fun accessDeniedException() {
        val exception = mock(AccessDeniedException::class.java)
        `when`(exception.message).thenReturn("권한이 없습니다.")

        val response: ResponseEntity<ErrorResponse> = exceptionHandler.handleAccessDeniedException(exception)

        assertEquals(403, response.statusCode.value())
        assertEquals("403-FORBIDDEN", response.body?.errorCode)
        assertEquals("권한이 없습니다.", response.body?.msg)
    }

    @Test
    @DisplayName("405 - 지원하지 않는 HTTP 메서드 예외 처리 테스트")
    fun methodNotAllowed() {
        val exception = HttpRequestMethodNotSupportedException("PUT")

        val response: ResponseEntity<ErrorResponse> = exceptionHandler.handleMethodNotAllowed(exception)

        assertEquals(405, response.statusCode.value())
        assertEquals("405", response.body?.errorCode)
        assertEquals("지원하지 않는 HTTP 메서드입니다. 사용 가능한 메서드: 알 수 없음", response.body?.msg)
    }

    @Test
    @DisplayName("500 - 서버 내부 오류 예외 처리 테스트")
    fun genericException() {
        val exception = Exception("서버 내부 오류 발생")

        val response: ResponseEntity<ErrorResponse> = exceptionHandler.handleGenericException(exception)

        assertEquals(500, response.statusCode.value())
        assertEquals("500", response.body?.errorCode)
        assertEquals("서버 내부 오류가 발생했습니다.", response.body?.msg)
    }

    @Test
    @DisplayName("CustomException 예외 처리 테스트")
    fun customException() {
        val exception = CustomException(UserErrorCode.INVALID_CREDENTIALS)

        val response: ResponseEntity<ErrorResponse> = exceptionHandler.handleCustomException(exception)

        assertEquals(UserErrorCode.INVALID_CREDENTIALS.status.value(), response.statusCode.value())
        assertEquals(UserErrorCode.INVALID_CREDENTIALS.code, response.body?.errorCode)
        assertEquals(UserErrorCode.INVALID_CREDENTIALS.message, response.body?.msg)
    }
}
