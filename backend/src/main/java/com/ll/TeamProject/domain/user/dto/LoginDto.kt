package com.ll.TeamProject.domain.user.dto

data class LoginDto(
    val item: UserDto,
    val apiKey: String,
    val accessToken: String
)
