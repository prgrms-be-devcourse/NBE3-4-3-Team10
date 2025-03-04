package com.ll.TeamProject.domain.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class VerificationCodeRequest(
    @field:NotBlank val username: String,
    @field:Email val email: String
)

