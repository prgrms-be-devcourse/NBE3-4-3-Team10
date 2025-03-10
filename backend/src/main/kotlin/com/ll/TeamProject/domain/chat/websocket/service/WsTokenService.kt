package com.ll.TeamProject.domain.chat.websocket.service

import com.ll.TeamProject.domain.user.entity.SiteUser
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class WsTokenService {

    // wsToken과 사용자 매핑 저장소 (유효 기간 추가)
    private val tokenStore = ConcurrentHashMap<String, Pair<SiteUser, Instant>>()

    /**
     * 사용자에 대한 웹소켓 전용 토큰을 생성하고 저장.
     * @param user 발급 대상 사용자
     * @return 발급된 wsToken
     */
    fun createWsTokenForUser(user: SiteUser): String {
        val token = UUID.randomUUID().toString()
        val expirationTime = Instant.now().plusSeconds(600) // 10분 후 만료
        tokenStore[token] = Pair(user, expirationTime)
        return token
    }

    /**
     * wsToken으로 사용자 정보 조회 (삭제하지 않음).
     * @param wsToken 클라이언트가 전달한 웹소켓 전용 토큰
     * @return 조회된 사용자 정보 (없으면 null)
     */
    fun getUserFromWsToken(wsToken: String): SiteUser? {
        cleanupExpiredTokens()
        return tokenStore[wsToken]?.first
    }

    /**
     * wsToken 유효성 체크 (유효 시간 고려).
     * @param wsToken 클라이언트가 전달한 웹소켓 전용 토큰
     * @return 유효 여부
     */
    fun isValidWsToken(wsToken: String): Boolean {
        cleanupExpiredTokens()
        return tokenStore.containsKey(wsToken)
    }

    /**
     * WebSocket 연결 종료 시 토큰 제거.
     * @param wsToken 클라이언트가 전달한 웹소켓 전용 토큰
     */
    fun removeWsToken(wsToken: String) {
        tokenStore.remove(wsToken)
    }

    /**
     * 만료된 토큰 정리 (10분이 지난 토큰 삭제)
     */
    private fun cleanupExpiredTokens() {
        val now = Instant.now()
        tokenStore.entries.removeIf { it.value.second.isBefore(now) }
    }
}
