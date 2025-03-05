package com.ll.TeamProject.domain.user.controller;

import com.ll.TeamProject.domain.user.dto.ModifyUserReqBody;
import com.ll.TeamProject.domain.user.dto.UserDto;
import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.domain.user.service.UserService;
import com.ll.TeamProject.global.rsData.ResponseDto;
import com.ll.TeamProject.global.userContext.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
@Tag(name = "UserController", description = "사용자 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserContext userContext;
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "내 정보")
    public ResponseEntity<UserDto> me() {
        SiteUser actor = userContext.findActor().get();

        return ResponseEntity.status(HttpStatus.OK).body(new UserDto(actor));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "회원 탈퇴 (soft)")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {
        userService.delete(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping
    @Operation(summary = "내정보 수정")
    public ResponseEntity<ResponseDto<Void>> modifyUser(@RequestBody @Valid ModifyUserReqBody reqbody) {
        userService.modify(reqbody.nickname());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success("사용자 정보가 수정되었습니다."));
    }
}
