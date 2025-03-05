package com.ll.TeamProject.domain.user.service

import com.ll.TeamProject.domain.user.dto.ForbiddenNicknameList
import com.ll.TeamProject.domain.user.repository.ForbiddenRepository
import org.springframework.stereotype.Service

@Service
class ForbiddenService(
    private val forbiddenRepository: ForbiddenRepository
) {
    fun isForbidden(nickname: String): Boolean {
        val forbiddenList = ForbiddenNicknameList(forbiddenRepository.findAll())
        return forbiddenList.contains(nickname)
    }
}
