package com.ll.TeamProject.global.exceptions;

import com.ll.TeamProject.domain.user.exceptions.UserErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final UserErrorCode errorCode;

    public CustomException(UserErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(UserErrorCode errorCode, String detail) {
        super(detail);
        this.errorCode = errorCode;
    }
}