package com.ll.TeamProject.global.userContext

import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.global.exceptions.ServiceException
import org.springframework.stereotype.Service

@Service
class UserContextService(
    private val userContext: UserContext
) {
    fun getAuthenticatedUser(): SiteUser {
        return userContext.findActor() ?: throw ServiceException("401", "로그인을 먼저 해주세요!")
    }
}
