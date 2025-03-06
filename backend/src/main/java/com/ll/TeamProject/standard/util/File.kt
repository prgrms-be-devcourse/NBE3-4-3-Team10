package com.ll.TeamProject.standard.util

import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

object File {
    fun downloadByHttp(url: String, dirPath: String) {
        try {
            val client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build()
            // 먼저 헤더만 가져오기 위한 HEAD 요청
            val headResponse = client.send(
                HttpRequest.newBuilder(URI.create(url))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build(),
                HttpResponse.BodyHandlers.discarding()
            )
            // 실제 파일 다운로드
            val response = client.send(
                request,
                HttpResponse.BodyHandlers.ofFile(
                    createTargetPath(url, dirPath, headResponse)
                )
            )
        } catch (e: IOException) {
            throw RuntimeException("다운로드 중 오류 발생: " + e.message, e)
        } catch (e: InterruptedException) {
            throw RuntimeException("다운로드 중 오류 발생: " + e.message, e)
        }
    }

    private fun createTargetPath(url: String, dirPath: String, response: HttpResponse<*>): Path {
        // 디렉토리가 없으면 생성
        val directory = Path.of(dirPath)
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory)
            } catch (e: IOException) {
                throw RuntimeException("디렉토리 생성 실패: " + e.message, e)
            }
        }
        // 파일명 생성
        val filename = getFilenameFromUrl(url)
        val extension = getExtensionFromResponse(response)
        return directory.resolve(filename + extension)
    }

    private fun getFilenameFromUrl(url: String): String {
        try {
            val path = URI(url).path
            val filename = Path.of(path).fileName.toString()
            // 확장자 제거
            return if (filename.contains("."))
                filename.substring(0, filename.lastIndexOf('.'))
            else
                filename
        } catch (e: URISyntaxException) {
            // URL에서 파일명을 추출할 수 없는 경우 타임스탬프 사용
            return "download_" + System.currentTimeMillis()
        }
    }

    private fun getExtensionFromResponse(response: HttpResponse<*>): String {
        return response.headers()
            .firstValue("Content-Type")
            .map { contentType: String ->
                when (contentType.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                    .trim { it <= ' ' }.lowercase(Locale.getDefault())) {
                    "application/json" -> ".json"
                    "text/plain" -> ".txt"
                    "text/html" -> ".html"
                    "image/jpeg" -> ".jpg"
                    "image/png" -> ".png"
                    "application/pdf" -> ".pdf"
                    "application/xml" -> ".xml"
                    "application/zip" -> ".zip"
                    else -> ""
                }
            }
            .orElse("")
    }
}
