package com.ll.TeamProject.domain.user.dto

import jakarta.validation.constraints.NotBlank

data class PasswordChangeRequest(
    @field:NotBlank val password: String
)
