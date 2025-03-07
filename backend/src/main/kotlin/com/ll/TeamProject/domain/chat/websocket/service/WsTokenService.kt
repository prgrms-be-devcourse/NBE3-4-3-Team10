package com.ll.TeamProject.domain.chat.websocket.service

import com.ll.TeamProject.domain.user.entity.SiteUser
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class WsTokenService {

    // wsToken과 사용자 매핑 저장소 (임시 메모리 저장 방식)
    private val tokenStore = ConcurrentHashMap<String, SiteUser>()

    /**
     * 사용자에 대한 웹소켓 전용 토큰을 생성하고 저장.
     * @param user 발급 대상 사용자
     * @return 발급된 wsToken
     */
    fun createWsTokenForUser(user: SiteUser): String {
        val token = UUID.randomUUID().toString()
        tokenStore[token] = user
        return token
    }

    /**
     * wsToken으로 사용자 정보 조회 후, 1회성으로 제거 (재사용 방지).
     * @param wsToken 클라이언트가 전달한 웹소켓 전용 토큰
     * @return 조회된 사용자 정보 (없으면 null)
     */
    fun getUserFromWsToken(wsToken: String): SiteUser? {
        return tokenStore.remove(wsToken)  // 1회용이므로 조회 즉시 삭제
    }

    /**
     * wsToken 유효성 체크.
     * @param wsToken 클라이언트가 전달한 웹소켓 전용 토큰
     * @return 유효 여부
     */
    fun isValidWsToken(wsToken: String): Boolean {
        return tokenStore.containsKey(wsToken)
    }
}
