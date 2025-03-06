package com.ll.TeamProject.domain.user.dto

import com.ll.TeamProject.domain.user.entity.ForbiddenNickname

class ForbiddenNicknameList(forbiddenNames: List<ForbiddenNickname>) {
    private val forbiddenList: Set<String> = forbiddenNames
        .map { it.forbiddenName.lowercase() }
        .toSet()

    fun contains(nickname: String): Boolean {
        return forbiddenList.any { nickname.lowercase().contains(it) }
    }
}
