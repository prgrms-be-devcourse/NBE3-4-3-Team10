package com.ll.TeamProject.domain.chat.websocket

import com.ll.TeamProject.domain.user.entity.SiteUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Component
class ChatSessionManager {

    private val log = LoggerFactory.getLogger(ChatSessionManager::class.java)

    private val sessionsByCalendar = ConcurrentHashMap<Long, MutableSet<WebSocketSession>>()
    private val userSessionMap = ConcurrentHashMap<WebSocketSession, SiteUser>()

    fun addSession(calendarId: Long, session: WebSocketSession, user: SiteUser) {
        sessionsByCalendar.computeIfAbsent(calendarId) { ConcurrentHashMap.newKeySet() }.add(session)
        userSessionMap[session] = user
        log.info("📌 세션 추가됨 calendarId=$calendarId, user=${user.username}, session=${session.id}")
    }

    fun removeSession(calendarId: Long, session: WebSocketSession) {
        sessionsByCalendar[calendarId]?.remove(session)
        if (sessionsByCalendar[calendarId]?.isEmpty() == true) sessionsByCalendar.remove(calendarId)
        userSessionMap.remove(session)
        log.info("📌 세션 제거됨 calendarId=$calendarId, session=${session.id}")
    }

    fun broadcastMessage(calendarId: Long, message: TextMessage) {
        log.info("📡 [브로드캐스트] calendarId=$calendarId, 메시지=${message.payload}")

        sessionsByCalendar[calendarId]?.forEach { session ->
            if (session.isOpen) {
                session.sendMessage(message)
            }
        }
    }


    fun getUser(session: WebSocketSession): SiteUser? {
        return userSessionMap[session]
    }
}
