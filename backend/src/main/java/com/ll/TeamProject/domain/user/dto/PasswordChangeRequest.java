package com.ll.TeamProject.domain.user.dto;

import lombok.NonNull;

public record PasswordChangeRequest(
        @NonNull String password
) {}
