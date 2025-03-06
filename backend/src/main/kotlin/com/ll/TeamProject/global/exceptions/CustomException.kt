package com.ll.TeamProject.global.exceptions

open class CustomException @JvmOverloads constructor(
    val errorCode: ErrorCode,
    message: String = errorCode.message // 기본값을 errorCode의 메시지로 설정
) : RuntimeException(message)
