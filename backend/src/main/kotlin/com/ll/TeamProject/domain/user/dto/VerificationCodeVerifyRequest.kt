package com.ll.TeamProject.domain.user.dto

import jakarta.validation.constraints.NotBlank

data class VerificationCodeVerifyRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val verificationCode: String
)
