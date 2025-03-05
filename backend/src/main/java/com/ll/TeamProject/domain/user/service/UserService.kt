package com.ll.TeamProject.domain.user.service

import com.ll.TeamProject.domain.user.dto.UserDto
import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.enums.Role
import com.ll.TeamProject.domain.user.exceptions.UserErrorCode
import com.ll.TeamProject.domain.user.repository.UserRepository
import com.ll.TeamProject.global.exceptions.CustomException
import com.ll.TeamProject.global.userContext.UserContext
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val authTokenService: AuthTokenService,
    private val userContext: UserContext,
    private val forbiddenService: ForbiddenService,
    private val authService: AuthService
) {
    fun findByUsername(username: String): Optional<SiteUser> {
        return userRepository.findByUsername(username)
    }

    fun findByApiKey(apiKey: String): Optional<SiteUser> {
        return userRepository.findByApiKey(apiKey)
    }

    fun findById(id: Long): Optional<SiteUser> {
        return userRepository.findById(id)
    }

    fun findUsers(
        searchKeywordType: String,
        searchKeyword: String,
        page: Int,
        pageSize: Int,
        role: Role
    ): Page<SiteUser> {
        if (page < 1) throw CustomException(UserErrorCode.INVALID_PAGE_NUMBER)

        val pageRequest = PageRequest.of(page - 1, pageSize)

        if (searchKeyword.isBlank()) return findUsersNoKeyword(pageRequest, role)

        val keywordWithWildcards = "%$searchKeyword%"

        return when (searchKeywordType) {
            "email" -> userRepository.findByRoleAndEmailLikeAndIsDeletedFalse(role, keywordWithWildcards, pageRequest)
            else -> userRepository.findByRoleAndUsernameLikeAndIsDeletedFalse(role, keywordWithWildcards, pageRequest)
        }
    }

    fun findUsersNoKeyword(pageRequest: PageRequest, role: Role): Page<SiteUser> {
        return userRepository.findByRoleAndIsDeletedFalse(role, pageRequest)
    }

    fun modify(nickname: String) {
        if (forbiddenService.isForbidden(nickname)) {
            throw CustomException(UserErrorCode.FORBIDDEN_NICKNAME)
        }

        val actor = userContext.findActor().get()

        try {
            actor.changeNickname(nickname)
            userRepository.save(actor)
        } catch (exception: DataIntegrityViolationException) {
            throw CustomException(UserErrorCode.DUPLICATE_NICKNAME)
        }

        // 수정된 닉네임 바로 적용되도록 쿠키 수정
        userContext.makeAuthCookies(actor)
    }

    fun delete(id: Long): UserDto {
        val userOptional = findById(id)
        if (userOptional.isEmpty) {
            throw CustomException(UserErrorCode.USER_NOT_FOUND)
        }
        val userToDelete = userOptional.get()

        validatePermission(userToDelete)

        userToDelete.delete()
        userRepository.save(userToDelete)

        // TODO: 로그아웃 추가 수정 필요
        userContext.deleteCookie("accessToken")
        userContext.deleteCookie("apiKey")
        userContext.deleteCookie("JSESSIONID")

        authService.logout()
        return UserDto(userToDelete)
    }

    fun validatePermission(userToDelete: SiteUser) {
        val actor = userContext.actor
        if (actor.username == "admin") return

        if (userToDelete.username != actor.username) {
            throw CustomException(UserErrorCode.PERMISSION_DENIED)
        }
    }

    fun unlockAccount(id: Long) {
        val user = findById(id).get()
        user.unlockAccount()
        userRepository.save(user)
    }

    fun genAccessToken(user: SiteUser): String {
        return authTokenService.genAccessToken(user)
    }

    fun genAuthToken(user: SiteUser): String {
        return user.apiKey + " " + genAccessToken(user)
    }

    // JWT 로 얻은 가짜 user 객체 (DB 에서 조회한 user 아님)
    fun getUserFromAccessToken(accessToken: String): SiteUser? {
        val payload = authTokenService.payload(accessToken) ?: return null

        val id = payload["id"] as Long
        val username = payload["username"] as String
        val nickname = payload["nickname"] as String
        val role = payload["role"] as Role

        return SiteUser(id, username, nickname, role)
    }
}