package com.ll.TeamProject.global.globalExceptionHandler

import com.ll.TeamProject.global.exceptions.CustomException
import com.ll.TeamProject.global.exceptions.ServiceException
import com.ll.TeamProject.global.rsData.RsData
import com.ll.TeamProject.standard.base.Empty
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class GlobalExceptionHandler {

    private val log: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(NoSuchElementException::class)
    fun handle(ex: NoSuchElementException): ResponseEntity<ErrorResponse> {
        log.error("NoSuchElementException 발생:", ex)
        return ResponseEntity(ErrorResponse("404", "해당 데이터가 존재하지 않습니다."), HttpStatus.NOT_FOUND)
    }

    // 400 - 요청 데이터 유효성 검증 실패 (@Valid 사용)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errorMessage = ex.bindingResult
            .fieldErrors
            .firstOrNull()
            ?.let { "${it.field} ${it.defaultMessage}" }
            ?: "잘못된 요청입니다."

        log.error("ValidationException: {}", errorMessage)
        return ResponseEntity.badRequest().body(ErrorResponse("400-VALIDATION", errorMessage))
    }

    // 400 - 요청 데이터 타입 오류
    @ExceptionHandler(TypeMismatchException::class)
    fun handleTypeMismatchException(ex: TypeMismatchException): ResponseEntity<ErrorResponse> {
        log.error("TypeMismatchException: {}", ex.message)
        return ResponseEntity.badRequest().body(ErrorResponse("400-TYPE", "잘못된 데이터 타입입니다."))
    }

    // 400 - 필수 요청 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParamException(ex: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> {
        log.error("MissingServletRequestParameterException: {}", ex.message)
        return ResponseEntity.badRequest().body(ErrorResponse("400-MISSING_PARAM", "필수 요청 파라미터가 누락되었습니다."))
    }

    // 400 - 요청 JSON 파싱 오류
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParsingException(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        log.error("HttpMessageNotReadableException: {}", ex.message)
        return ResponseEntity.badRequest().body(ErrorResponse("400-JSON_PARSE", "잘못된 JSON 형식입니다."))
    }

    // 403 - 권한 없음
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        log.warn("AccessDeniedException: {}", ex.message)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse("403-FORBIDDEN", "권한이 없습니다."))
    }

    // 405 - 지원하지 않는 HTTP 메서드
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowed(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorResponse> {
        val supportedMethods = ex.supportedHttpMethods?.joinToString(", ") ?: "알 수 없음"

        log.error("HttpRequestMethodNotSupportedException: {} (지원 가능: {})", ex.method, supportedMethods)
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(ErrorResponse("405", "지원하지 않는 HTTP 메서드입니다. 사용 가능한 메서드: $supportedMethods"))
    }

    // 500 - 알 수 없는 서버 오류 (최종 예외 핸들러)
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unknown Exception: ", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("500", "서버 내부 오류가 발생했습니다."))
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException): ResponseEntity<ErrorResponse> {
        val errorCode = ex.errorCode

        log.error("CustomException 발생 - 코드: {} 메시지: {}", errorCode.code, errorCode.message)
        return ResponseEntity(ErrorResponse(errorCode.code, errorCode.message), errorCode.status)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServiceException::class)
    fun handle(ex: ServiceException): ResponseEntity<RsData<Empty>> {
        val rsData = ex.rsData
        return ResponseEntity.status(rsData.statusCode).body(rsData)
    }
}
