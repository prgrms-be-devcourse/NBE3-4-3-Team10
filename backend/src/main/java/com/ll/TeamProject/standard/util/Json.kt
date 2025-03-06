package com.ll.TeamProject.standard.util

import com.fasterxml.jackson.databind.ObjectMapper

object Json {
    private val objectMapper = ObjectMapper()

    fun toString(obj: Any?): String = runCatching {
        objectMapper.writeValueAsString(obj)
    }.getOrElse { e ->
        throw RuntimeException("JSON 직렬화 실패", e)
    }
}
