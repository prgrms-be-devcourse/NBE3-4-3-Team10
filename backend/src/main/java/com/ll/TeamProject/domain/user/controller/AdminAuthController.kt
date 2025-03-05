package com.ll.TeamProject.domain.user.controller;

import com.ll.TeamProject.domain.user.dto.LoginDto;
import com.ll.TeamProject.domain.user.dto.UserLoginReqBody;
import com.ll.TeamProject.domain.user.dto.VerificationCodeRequest;
import com.ll.TeamProject.domain.user.dto.VerificationCodeVerifyRequest;
import com.ll.TeamProject.domain.user.service.AccountVerificationService;
import com.ll.TeamProject.domain.user.service.AuthService;
import com.ll.TeamProject.global.rsData.ResponseDto;
import com.ll.TeamProject.global.userContext.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "AdminAuthController", description = "관리자 로그인 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
public class AdminAuthController {

    private final AuthService authService;
    private final AccountVerificationService accountVerificationService;
    private final UserContext userContext;

    @PostMapping("/login")
    @Transactional
    @Operation(summary = "관리자 로그인")
    public ResponseEntity<ResponseDto<LoginDto>> login(@RequestBody @Valid UserLoginReqBody req) {

        LoginDto loginDto = authService.login(req.username(), req.password());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(
                        "%s님 환영합니다.".formatted(loginDto.item().nickname()),
                        loginDto
                )
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public ResponseEntity<Void> logout() {

        // TODO: 로그아웃 추가 수정 필요
        userContext.deleteCookie("accessToken");
        userContext.deleteCookie("apiKey");
        userContext.deleteCookie("JSESSIONID");

        authService.logout();

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/verification-codes")
    @Operation(summary = "인증번호 발송")
    public ResponseEntity<Void> sendVerification(@RequestBody @Valid VerificationCodeRequest req) {

        accountVerificationService.processVerification(req.username(), req.email());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/verification-codes/verify")
    @Operation(summary = "관리자 계정 잠김 이메일 인증")
    public ResponseEntity<Void> verificationAdminAccount(@RequestBody @Valid VerificationCodeVerifyRequest req) {
        accountVerificationService.verifyAndUnlockAccount(req.username(), req.verificationCode());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
