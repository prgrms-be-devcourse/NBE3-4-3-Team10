package com.ll.TeamProject.domain.user.exceptions;

import com.ll.TeamProject.global.exceptions.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "USER_001", "아이디 또는 비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_002", "존재하지 않는 사용자입니다."),
    FORBIDDEN_NICKNAME(HttpStatus.BAD_REQUEST, "USER_003", "해당 닉네임은 사용할 수 없습니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "USER_004", "이미 사용중인 닉네임입니다."),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "USER_005", "접근 권한이 없습니다."),
    INVALID_USERNAME_OR_EMAIL(HttpStatus.NOT_FOUND, "USER_006", "아이디 또는 이메일이 일치하지 않습니다."),

    INVALID_PAGE_NUMBER(HttpStatus.BAD_REQUEST, "ADMIN_001", "페이지 번호는 1 이상이어야 합니다."),

    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "AUTH_001", "계정이 잠겨있습니다."),
    VERIFICATION_CODE_MISMATCH(HttpStatus.UNAUTHORIZED, "AUTH_002", "인증번호가 일치하지 않습니다."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_003", "인증이 만료되었습니다."),
    INVALID_REQUEST(HttpStatus.FORBIDDEN, "AUTH_004", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_005", "사용자 인증정보가 올바르지 않습니다."),

    EMAIL_SEND_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_001", "이메일 전송에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

