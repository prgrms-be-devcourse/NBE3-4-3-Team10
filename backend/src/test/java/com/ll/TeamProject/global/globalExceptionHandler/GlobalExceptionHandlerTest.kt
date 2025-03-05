package com.ll.TeamProject.global.globalExceptionHandler

import com.ll.TeamProject.domain.user.exceptions.UserErrorCode
import com.ll.TeamProject.global.exceptions.CustomException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.MethodParameter
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ExtendWith(MockitoExtension::class)
internal class GlobalExceptionHandlerTest {
    @InjectMocks
    private lateinit var exceptionHandler: GlobalExceptionHandler

    @Test
    @DisplayName("404 - NoSuchElementException 예외 처리 테스트")
    fun noSuchElementException() {
        val exception = NoSuchElementException("해당 데이터가 존재하지 않습니다.")

        val response = exceptionHandler.handle(exception)

        Assertions.assertEquals(404, response.statusCode.value())
        Assertions.assertEquals("404", response.body!!.errorCode)
        Assertions.assertEquals("해당 데이터가 존재하지 않습니다.", response.body!!.msg)
    }

    @Test
    @DisplayName("400 - 유효성 검증 실패 예외 처리 테스트")
    fun validationException() {
        // 가짜 필드 에러 생성
        val fieldError = FieldError("user", "username", "필수 입력값입니다.")
        val bindingResult = Mockito.mock(BindingResult::class.java)

        Mockito.`when`(bindingResult.fieldErrors).thenReturn(listOf(fieldError))

        // 가짜 메서드 생성 (파라미터 없는 메서드)
        val method = String::class.java.getDeclaredMethod("toString")
        val methodParameter = MethodParameter(method, -1)

        val exception = MethodArgumentNotValidException(methodParameter, bindingResult)

        val response = exceptionHandler.handleValidationException(exception)

        Assertions.assertEquals(400, response.statusCode.value())
        Assertions.assertEquals("400-VALIDATION", response.body!!.errorCode)
        Assertions.assertEquals("username 필수 입력값입니다.", response.body!!.msg)
    }

    @Test
    @DisplayName("400 - 데이터 타입 오류 예외 처리 테스트")
    fun typeMismatchException() {
        val exception = Mockito.mock(
            MethodArgumentTypeMismatchException::class.java
        )
        Mockito.`when`(exception.message).thenReturn("잘못된 데이터 타입입니다.")

        val response = exceptionHandler.handleTypeMismatchException(exception)

        Assertions.assertEquals(400, response.statusCode.value())
        Assertions.assertEquals("400-TYPE", response.body!!.errorCode)
        Assertions.assertEquals("잘못된 데이터 타입입니다.", response.body!!.msg)
    }

    @Test
    @DisplayName("403 - 권한 없음 예외 처리 테스트")
    fun accessDeniedException() {
        val exception = AccessDeniedException("권한이 없습니다.")

        val response = exceptionHandler.handleAccessDeniedException(exception)

        Assertions.assertEquals(403, response.statusCode.value())
        Assertions.assertEquals("403-FORBIDDEN", response.body!!.errorCode)
        Assertions.assertEquals("권한이 없습니다.", response.body!!.msg)
    }

    @Test
    @DisplayName("405 - 지원하지 않는 HTTP 메서드 예외 처리 테스트")
    fun methodNotAllowed() {
        val exception = HttpRequestMethodNotSupportedException("PUT")

        val response = exceptionHandler.handleMethodNotAllowed(exception)

        Assertions.assertEquals(405, response.statusCode.value())
        Assertions.assertEquals("405", response.body!!.errorCode)
        Assertions.assertEquals("지원하지 않는 HTTP 메서드입니다. 사용 가능한 메서드: 알 수 없음", response.body!!.msg)
    }

    @Test
    @DisplayName("500 - 서버 내부 오류 예외 처리 테스트")
    fun genericException() {
        val exception = Exception("서버 내부 오류 발생")

        val response = exceptionHandler.handleGenericException(exception)

        Assertions.assertEquals(500, response.statusCode.value())
        Assertions.assertEquals("500", response.body!!.errorCode)
        Assertions.assertEquals("서버 내부 오류가 발생했습니다.", response.body!!.msg)
    }

    @Test
    @DisplayName("CustomException 예외 처리 테스트")
    fun customException() {
        val exception = CustomException(UserErrorCode.INVALID_CREDENTIALS)

        val response = exceptionHandler.handleCustomException(exception)

        Assertions.assertEquals(UserErrorCode.INVALID_CREDENTIALS.status.value(), response.statusCode.value())
        Assertions.assertEquals(UserErrorCode.INVALID_CREDENTIALS.code, response.body!!.errorCode)
        Assertions.assertEquals(UserErrorCode.INVALID_CREDENTIALS.message, response.body!!.msg)
    }
}

