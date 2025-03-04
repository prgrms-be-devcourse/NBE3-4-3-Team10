package com.ll.TeamProject.domain.user.dto;

public record LoginDto(
        UserDto item,
        String apiKey,
        String accessToken
) { }
