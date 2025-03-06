package com.ll.TeamProject.domain.user.controller

import com.ll.TeamProject.domain.user.dto.UserDto
import com.ll.TeamProject.domain.user.enums.Role
import com.ll.TeamProject.domain.user.service.UserService
import com.ll.TeamProject.global.rsData.ResponseDto
import com.ll.TeamProject.standard.page.dto.PageDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "AdminUserController", description = "관리자 회원조회 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
class AdminUserController(
    private val userService: UserService
) {

    @GetMapping
    @Operation(summary = "회원 명단 조회 (페이징, 검색)")
    fun users(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(defaultValue = "username") searchKeywordType: String,
        @RequestParam(defaultValue = "") searchKeyword: String
    ): ResponseEntity<ResponseDto<PageDto<UserDto>>> {
        val users = userService.findUsers(searchKeywordType, searchKeyword, page, pageSize, Role.USER)
            .map { UserDto(it) }
        return ResponseEntity.ok(ResponseDto.success("조회 완료", PageDto(users)))
    }

    @GetMapping("/admins")
    @Operation(summary = "관리자 명단 조회 (페이징, 검색)")
    fun admins(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(defaultValue = "username") searchKeywordType: String,
        @RequestParam(defaultValue = "") searchKeyword: String
    ): ResponseEntity<ResponseDto<PageDto<UserDto>>> {
        val admins = userService.findUsers(searchKeywordType, searchKeyword, page, pageSize, Role.ADMIN)
            .map { UserDto(it) }
        return ResponseEntity.ok(ResponseDto.success("조회 완료", PageDto(admins)))
    }
}
