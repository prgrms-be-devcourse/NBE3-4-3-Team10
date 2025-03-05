package com.ll.TeamProject.global.globalExceptionHandler;

import com.ll.TeamProject.domain.user.exceptions.UserErrorCode;
import com.ll.TeamProject.global.exceptions.CustomException;
import com.ll.TeamProject.global.exceptions.ServiceException;
import com.ll.TeamProject.global.rsData.RsData;
import com.ll.TeamProject.standard.base.Empty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handle(NoSuchElementException ex) {

        log.error("NoSuchElementException 발생:", ex);

        ErrorResponse errorResponse = new ErrorResponse("404", "해당 데이터가 존재하지 않습니다.");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 400 - 요청 데이터 유효성 검증 실패 (@Valid 사용)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");

        log.error("ValidationException: {}", errorMessage);
        return ResponseEntity.badRequest().body(new ErrorResponse("400-VALIDATION", errorMessage));
    }

    // 400 - 요청 데이터 타입 오류
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(TypeMismatchException ex) {
        log.error("TypeMismatchException: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse("400-TYPE", "잘못된 데이터 타입입니다."));
    }

    // 400 - 필수 요청 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParamException(MissingServletRequestParameterException ex) {
        log.error("MissingServletRequestParameterException: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse("400-MISSING_PARAM", "필수 요청 파라미터가 누락되었습니다."));
    }

    // 400 - 요청 JSON 파싱 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParsingException(HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse("400-JSON_PARSE", "잘못된 JSON 형식입니다."));
    }

    // 403 - 권한 없음
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("AccessDeniedException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("403-FORBIDDEN", "권한이 없습니다."));
    }

    // 405 - 지원하지 않는 HTTP 메서드
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        String supportedMethods = ex.getSupportedHttpMethods() != null
                ? ex.getSupportedHttpMethods().toString()
                : "알 수 없음";

        log.error("HttpRequestMethodNotSupportedException: {} (지원 가능: {})", ex.getMethod(), supportedMethods);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse("405", "지원하지 않는 HTTP 메서드입니다. 사용 가능한 메서드: " + supportedMethods));
    }

    // 500 - 알 수 없는 서버 오류 (최종 예외 핸들러)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unknown Exception: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("500", "서버 내부 오류가 발생했습니다."));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        UserErrorCode errorCode = ex.getErrorCode();

        log.error("CustomException 발생 - 코드: {} 메시지: {}", errorCode.getCode(), errorCode.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<RsData<Empty>> handle(ServiceException ex) {
        RsData<Empty> rsData = ex.getRsData();

        return ResponseEntity
                .status(rsData.getStatusCode())
                .body(rsData);
    }
}