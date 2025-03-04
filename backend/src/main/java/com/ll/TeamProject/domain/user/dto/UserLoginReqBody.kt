package com.ll.TeamProject.domain.user.dto

import jakarta.validation.constraints.NotBlank

data class UserLoginReqBody(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String
)
