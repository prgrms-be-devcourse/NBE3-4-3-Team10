package com.ll.TeamProject.domain.user.controller

import com.ll.TeamProject.domain.user.dto.LoginDto
import com.ll.TeamProject.domain.user.dto.UserLoginReqBody
import com.ll.TeamProject.domain.user.dto.VerificationCodeRequest
import com.ll.TeamProject.domain.user.dto.VerificationCodeVerifyRequest
import com.ll.TeamProject.domain.user.service.AccountVerificationService
import com.ll.TeamProject.domain.user.service.AuthService
import com.ll.TeamProject.global.rsData.ResponseDto
import com.ll.TeamProject.global.userContext.UserContext
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
@Tag(name = "AdminAuthController", description = "관리자 로그인 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
class AdminAuthController(
    private val authService: AuthService,
    private val accountVerificationService: AccountVerificationService,
    private val userContext: UserContext,
) {

    @PostMapping("/login")
    @Transactional
    @Operation(summary = "관리자 로그인")
    fun login(@RequestBody @Valid req: UserLoginReqBody): ResponseEntity<ResponseDto<LoginDto>> {
        val loginDto = authService.login(req.username, req.password)
        return ResponseEntity.ok(
            ResponseDto.success(
                msg = "${loginDto.item.nickname}님 환영합니다.",
                data = loginDto
            )
        )
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    fun logout(): ResponseEntity<Void> {
        // TODO: 로그아웃 추가 수정 필요
        userContext.deleteCookie("accessToken")
        userContext.deleteCookie("apiKey")
        userContext.deleteCookie("JSESSIONID")

        authService.logout()
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/verification-codes")
    @Operation(summary = "인증번호 발송")
    fun sendVerification(@RequestBody @Valid req: VerificationCodeRequest): ResponseEntity<Void> {
        accountVerificationService.processVerification(req.username, req.email)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/verification-codes/verify")
    @Operation(summary = "관리자 계정 잠김 이메일 인증")
    fun verificationAdminAccount(@RequestBody @Valid req: VerificationCodeVerifyRequest): ResponseEntity<Void> {
        accountVerificationService.verifyAndUnlockAccount(req.username, req.verificationCode)
        return ResponseEntity.noContent().build()
    }
}