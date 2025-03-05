package com.ll.TeamProject.domain.user.controller;

import com.ll.TeamProject.domain.user.dto.UserDto;
import com.ll.TeamProject.domain.user.enums.Role;
import com.ll.TeamProject.domain.user.service.UserService;
import com.ll.TeamProject.global.rsData.ResponseDto;
import com.ll.TeamProject.standard.page.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "AdminUserController", description = "관리자 회원조회 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "회원 명단 조회 (페이징, 검색)")
    public ResponseEntity<ResponseDto<PageDto<UserDto>>> users(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "searchKeywordType", defaultValue = "username") String searchKeywordType,
            @RequestParam(name = "searchKeyword", defaultValue = "") String searchKeyword
    ) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success("조회 완료", new PageDto<>(
                        userService.findUsers(searchKeywordType, searchKeyword, page, pageSize, Role.USER)
                                .map(UserDto::new)
                        )
                )
        );
    }

    @GetMapping("/admins")
    @Operation(summary = "관리자 명단 조회 (페이징, 검색)")
    public ResponseEntity<ResponseDto<PageDto<UserDto>>> admins(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "searchKeywordType", defaultValue = "username") String searchKeywordType,
            @RequestParam(name = "searchKeyword", defaultValue = "") String searchKeyword
    ) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success("조회 완료", new PageDto<>(
                            userService.findUsers(searchKeywordType, searchKeyword, page, pageSize, Role.ADMIN)
                                    .map(UserDto::new)
                            )
                )
        );
    }
}
