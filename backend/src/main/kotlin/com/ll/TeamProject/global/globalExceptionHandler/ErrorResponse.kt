package com.ll.TeamProject.global.globalExceptionHandler

import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorResponse(
    @JsonProperty("errorCode")
    val errorCode: String,

    @JsonProperty("msg")
    val msg: String
)