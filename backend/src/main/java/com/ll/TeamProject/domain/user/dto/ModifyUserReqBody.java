package com.ll.TeamProject.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record ModifyUserReqBody (
        @NotBlank String nickname
) {}
