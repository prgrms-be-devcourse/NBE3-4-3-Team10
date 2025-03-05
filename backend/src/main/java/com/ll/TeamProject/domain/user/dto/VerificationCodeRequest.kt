package com.ll.TeamProject.domain.user.dto;

import lombok.NonNull;

public record VerificationCodeRequest(
        @NonNull String username,
        @NonNull String email
) { }
