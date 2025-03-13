package com.ll.TeamProject.global.rsData

import com.fasterxml.jackson.annotation.JsonIgnore

data class RsData<T>(
    val resultCode: String,
    val msg: String,
    val data: T? = null
) {

    // 상태 코드 추출
    @get:JsonIgnore
    val statusCode: Int
        get() = resultCode.split("-").first().toInt()

    companion object {
        fun <T> of(resultCode: String, msg: String, data: T): RsData<T> {
            return RsData(resultCode, msg, data)
        }

        fun okWithoutData(msg: String): RsData<Empty> {
            return RsData("S-1", msg, Empty())
        }

        fun failWithoutData(msg: String): RsData<Empty> {
            return RsData("F-1", msg, Empty())
        }
    }

    class Empty
}
