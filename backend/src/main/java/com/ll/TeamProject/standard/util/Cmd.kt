package com.ll.TeamProject.standard.util

import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

object Cmd {
    fun runAsync(cmd: String) {
        thread(start = true) { run(cmd) }
    }

    fun run(cmd: String) {
        cmd.takeIf { it.isNotBlank() }?.let {
            try {
                val processBuilder = ProcessBuilder("bash", "-c", it)
                val process = processBuilder.start()
                process.waitFor(1, TimeUnit.MINUTES)
            } catch (e: Exception) {
                System.err.println("Cmd 실행 중 오류 발생: ${e.message}")
                e.printStackTrace()
            }
        } ?: System.err.println("Cmd 실행 취소됨: 빈 문자열이거나 null 값이 전달됨")
    }
}
