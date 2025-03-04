package com.ll.TeamProject.global.globalExceptionHandler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    @JsonProperty("errorCode")
    private String errorCode;

    @JsonProperty("msg")
    private String msg;
}