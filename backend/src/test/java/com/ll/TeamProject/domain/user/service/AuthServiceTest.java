package com.ll.TeamProject.domain.user.service;

import com.ll.TeamProject.domain.user.exceptions.UserErrorCode;
import com.ll.TeamProject.domain.user.repository.UserRepository;
import com.ll.TeamProject.global.exceptions.CustomException;
import com.ll.TeamProject.global.userContext.UserContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Mockito 확장 사용
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserContext userContext;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("로그인시 잘못된 아이디")
    void t1() {
        // given
        String username = "nonexistentUser";
        String password = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> authService.login(username, password));

        assertEquals(UserErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
        assertEquals(UserErrorCode.INVALID_CREDENTIALS.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("로그인시 잘못된 비밀번호")
    void t2() {
        // given
        String username = "admin1";
        String password = "wrongPassword";
//        SiteUser user = new SiteUser(
//                username,
//                password
//        );

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> authService.login(username, password));

        assertEquals(UserErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
        assertEquals(UserErrorCode.INVALID_CREDENTIALS.getMessage(), exception.getMessage());

//        verify(authenticationService).handleLoginFailure(user); // 로그인 실패 처리 실행 확인
    }
}
