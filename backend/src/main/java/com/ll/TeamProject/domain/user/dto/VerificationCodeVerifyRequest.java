package com.ll.TeamProject.domain.user.dto;

import lombok.NonNull;

public record VerificationCodeVerifyRequest(
        @NonNull String username,
        @NonNull String verificationCode
) { }
