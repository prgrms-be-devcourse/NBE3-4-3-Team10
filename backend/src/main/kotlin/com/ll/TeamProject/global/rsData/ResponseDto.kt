package com.ll.TeamProject.global.rsData

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseDto<T>(
    val success: Boolean,  // 성공 여부
    val code: String,      // 내부 에러 코드 또는 성공 코드
    val msg: String,       // 응답 메시지
    val data: T? = null    // 실제 데이터 (성공 응답 시, 없을 수도 있음)
) {
    companion object {
        fun <T> success(msg: String, data: T): ResponseDto<T> {
            return ResponseDto(success = true, code = "SUCCESS", msg = msg, data = data)
        }

        fun success(msg: String): ResponseDto<Void> {
            return ResponseDto(success = true, code = "SUCCESS", msg = msg, data = null)
        }

        fun failure(code: String, msg: String): ResponseDto<Nothing> {
            return ResponseDto(success = false, code = code, msg = msg, data = null)
        }
    }
}