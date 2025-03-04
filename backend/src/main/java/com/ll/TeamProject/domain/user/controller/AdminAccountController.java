package com.ll.TeamProject.domain.user.controller;

import com.ll.TeamProject.domain.user.dto.PasswordChangeRequest;
import com.ll.TeamProject.domain.user.service.AccountVerificationService;
import com.ll.TeamProject.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
@Tag(name = "AdminAccountController", description = "관리자 계정 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
public class AdminAccountController {

    private final UserService userService;
    private final AccountVerificationService accountVerificationService;

    @PutMapping("/{username}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable("username") String username,
            @RequestBody @Valid PasswordChangeRequest request) {

        accountVerificationService.changePassword(username, request.password());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PatchMapping("/{id}/unlock")
    public ResponseEntity<Void> unlockAdmin(@PathVariable("id") Long id) {

        userService.unlockAccount(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
