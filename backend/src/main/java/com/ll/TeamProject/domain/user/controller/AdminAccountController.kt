package com.ll.TeamProject.domain.user.controller

import com.ll.TeamProject.domain.user.dto.PasswordChangeRequest
import com.ll.TeamProject.domain.user.service.AccountVerificationService
import com.ll.TeamProject.domain.user.service.UserService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
@Tag(name = "AdminAccountController", description = "관리자 계정 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
class AdminAccountController(
    private val userService: UserService,
    private val accountVerificationService: AccountVerificationService
) {

    @PutMapping("/{username}/password")
    fun changePassword(
        @PathVariable username: String,
        @RequestBody @Valid request: PasswordChangeRequest
    ): ResponseEntity<Void> {
        accountVerificationService.changePassword(username, request.password)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}/unlock")
    fun unlockAdmin(@PathVariable id: Long): ResponseEntity<Void> {
        userService.unlockAccount(id)
        return ResponseEntity.noContent().build()
    }
}
