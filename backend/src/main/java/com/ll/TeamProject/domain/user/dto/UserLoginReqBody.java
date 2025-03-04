package com.ll.TeamProject.domain.user.dto;

import lombok.NonNull;

public record UserLoginReqBody(
        @NonNull String username,
        @NonNull String password
) { }
