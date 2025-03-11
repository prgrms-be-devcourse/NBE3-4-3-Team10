package com.ll.TeamProject.domain.user.controller

import com.ll.TeamProject.domain.user.dto.ModifyUserReqBody
import com.ll.TeamProject.domain.user.dto.UserDto
import com.ll.TeamProject.domain.user.exceptions.UserErrorCode
import com.ll.TeamProject.domain.user.service.UserService
import com.ll.TeamProject.global.exceptions.CustomException
import com.ll.TeamProject.global.rsData.ResponseDto
import com.ll.TeamProject.global.userContext.UserContext
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
@Tag(name = "UserController", description = "사용자 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
class UserController(
    private val userContext: UserContext,
    private val userService: UserService
) {

    @GetMapping("/me")
    @Operation(summary = "내 정보")
    fun me(): ResponseEntity<UserDto> {
        val actor = userContext.findActor() ?: throw CustomException(UserErrorCode.UNAUTHORIZED)
        return ResponseEntity.ok(UserDto(actor))
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "회원 탈퇴 (soft)")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping
    @Operation(summary = "내정보 수정")
    fun modifyUser(@RequestBody @Valid reqbody: ModifyUserReqBody): ResponseEntity<ResponseDto<Void>> {
        userService.modify(reqbody.nickname)
        return ResponseEntity.ok(ResponseDto.success("사용자 정보가 수정되었습니다."))
    }

    @GetMapping("/findByUsername")
    @Operation(summary = "사용자 이름으로 사용자 조회")
    fun findByUsername(@RequestParam username: String): ResponseEntity<UserDto> {
        println("📌 요청된 username: $username")
        val user = userService.findByUsername(username)
            .orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }
        println("📌 찾은 사용자: ${user.username}")
        return ResponseEntity.ok(UserDto(user))
    }
}
