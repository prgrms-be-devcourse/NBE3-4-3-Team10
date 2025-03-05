package com.ll.TeamProject.domain.user.dto

import jakarta.validation.constraints.NotBlank

data class ModifyUserReqBody(
    @field:NotBlank val nickname: String
)
