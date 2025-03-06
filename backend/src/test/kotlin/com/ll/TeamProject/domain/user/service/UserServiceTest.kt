package com.ll.TeamProject.domain.user.service

import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.enums.Role
import com.ll.TeamProject.domain.user.exceptions.UserErrorCode
import com.ll.TeamProject.domain.user.repository.UserRepository
import com.ll.TeamProject.global.exceptions.CustomException
import com.ll.TeamProject.global.userContext.UserContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.test.context.support.WithMockUser

@ExtendWith(MockitoExtension::class)
internal class UserServiceTest {
    @InjectMocks
    private lateinit var userService: UserService

    @Mock
    private lateinit var authService: AuthService

    @Mock
    private lateinit var authTokenService: AuthTokenService

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var forbiddenService: ForbiddenService

    @Mock
    private lateinit var mockPage: Page<SiteUser>

    @Mock
    private lateinit var userContext: UserContext

    @Test
    @DisplayName("회원 명단 조회 - 빈 키워드")
    fun t3() {
        val searchKeywordType = "username"
        val searchKeyword = ""
        val page = 1
        val pageSize = 10
        val pageRequest = PageRequest.of(page - 1, pageSize)

        val spyService = Mockito.spy(userService)
        Mockito.doReturn(mockPage).`when`(spyService).findUsersNoKeyword(pageRequest, Role.USER)

        // when
        spyService.findUsers(searchKeywordType, searchKeyword, page, pageSize, Role.USER)

        // then
        Mockito.verify(spyService).findUsersNoKeyword(pageRequest, Role.USER)
    }

    @Test
    @DisplayName("회원 명단 조회 - 이메일로")
    fun t4() {
        // given
        val searchKeywordType = "email"
        val searchKeyword = "test@example.com"
        val page = 1
        val pageSize = 10
        val pageRequest = PageRequest.of(page - 1, pageSize)

        Mockito.`when`(
            userRepository.findByRoleAndEmailLikeAndIsDeletedFalse(
                Role.USER,
                "%$searchKeyword%", pageRequest
            )
        )
            .thenReturn(mockPage)

        // when
        userService.findUsers(searchKeywordType, searchKeyword, page, pageSize, Role.USER)

        // then
        Mockito.verify(userRepository)
            .findByRoleAndEmailLikeAndIsDeletedFalse(
                Role.USER,
                "%$searchKeyword%", pageRequest
            )
    }

    @Test
    @DisplayName("회원 명단 조회 - username 으로")
    fun t5() {
        // given
        val searchKeywordType = "username"
        val searchKeyword = "testUser"
        val page = 1
        val pageSize = 10
        val pageRequest = PageRequest.of(page - 1, pageSize)

        Mockito.`when`(
            userRepository.findByRoleAndUsernameLikeAndIsDeletedFalse(
                Role.USER,
                "%$searchKeyword%", pageRequest
            )
        )
            .thenReturn(mockPage)

        // when
        userService.findUsers(searchKeywordType, searchKeyword, page, pageSize, Role.USER)

        // then
        Mockito.verify(userRepository)
            .findByRoleAndUsernameLikeAndIsDeletedFalse(
                Role.USER,
                "%$searchKeyword%", pageRequest
            )
    }

    @Test
    @DisplayName("회원 명단 조회 - 잘못된 페이지")
    fun t6() {
        // given
        val searchKeywordType = "username"
        val searchKeyword = ""
        val page = 0
        val pageSize = 10

        // when & then
        val exception = Assertions.assertThrows(CustomException::class.java) {
            userService.findUsers(searchKeywordType, searchKeyword, page, pageSize, Role.USER)
        }

        Assertions.assertEquals(UserErrorCode.INVALID_PAGE_NUMBER, exception.errorCode)
        Assertions.assertEquals(UserErrorCode.INVALID_PAGE_NUMBER.message, exception.message)
    }

    @Test
    @DisplayName("회원 탈퇴 - 권한 없음")
    @WithMockUser(username = "actor", roles = ["USER"])
    fun t7() {
        val userToDelete = SiteUser("userToDelete")
        val actor = SiteUser("actor")

        Mockito.`when`(userContext.getActor())
            .thenReturn(actor)

        val exception = Assertions.assertThrows(CustomException::class.java) {
            userService.validatePermission(userToDelete)
        }

        Assertions.assertEquals(UserErrorCode.PERMISSION_DENIED, exception.errorCode)
        Assertions.assertEquals(UserErrorCode.PERMISSION_DENIED.message, exception.message)
    }
}
