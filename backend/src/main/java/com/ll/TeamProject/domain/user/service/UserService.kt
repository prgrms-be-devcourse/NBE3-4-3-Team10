package com.ll.TeamProject.domain.user.service;

import com.ll.TeamProject.domain.user.dto.UserDto;
import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.domain.user.enums.Role;
import com.ll.TeamProject.domain.user.exceptions.UserErrorCode;
import com.ll.TeamProject.domain.user.repository.UserRepository;
import com.ll.TeamProject.global.exceptions.CustomException;
import com.ll.TeamProject.global.userContext.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthTokenService authTokenService;
    private final UserContext userContext;
    private final ForbiddenService forbiddenService;
    private final AuthService authService;

    public Optional<SiteUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<SiteUser> findByApiKey(String apiKey) {
        return userRepository.findByApiKey(apiKey);
    }

    public Optional<SiteUser> findById(long id) {
        return userRepository.findById(id);
    }

    public Page<SiteUser> findUsers(
            String searchKeywordType,
            String searchKeyword,
            int page,
            int pageSize,
            Role role
    ) {
        if (page < 1) throw new CustomException(UserErrorCode.INVALID_PAGE_NUMBER);

        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);

        if (searchKeyword.isBlank()) return findUsersNoKeyword(pageRequest, role);

        searchKeyword = "%" + searchKeyword + "%";

        return switch (searchKeywordType) {
            case "email" ->
                    userRepository.findByRoleAndEmailLikeAndIsDeletedFalse(role, searchKeyword, pageRequest);
            default -> userRepository.findByRoleAndUsernameLikeAndIsDeletedFalse(role, searchKeyword, pageRequest);
        };
    }

    public Page<SiteUser> findUsersNoKeyword(PageRequest pageRequest, Role role) {
        return userRepository.findByRoleAndIsDeletedFalse(role, pageRequest);
    }

    public void modify(String nickname) {
        if (forbiddenService.isForbidden(nickname)) {
            throw new CustomException(UserErrorCode.FORBIDDEN_NICKNAME);
        }

        SiteUser actor = userContext.findActor().get();

        try {
            actor.changeNickname(nickname);
            userRepository.save(actor);
        } catch (DataIntegrityViolationException exception) {
            throw new CustomException(UserErrorCode.DUPLICATE_NICKNAME);
        }

        // 수정된 닉네임 바로 적용되도록 쿠키 수정
        userContext.makeAuthCookies(actor);
    }

    public UserDto delete(long id) {
        Optional<SiteUser> userOptional = findById(id);
        if (userOptional.isEmpty()) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }
        SiteUser userToDelete = userOptional.get();

        validatePermission(userToDelete);

        userToDelete.delete();
        userRepository.save(userToDelete);

        // TODO: 로그아웃 추가 수정 필요
        userContext.deleteCookie("accessToken");
        userContext.deleteCookie("apiKey");
        userContext.deleteCookie("JSESSIONID");

        authService.logout();
        return new UserDto(userToDelete);
    }

    public void validatePermission(SiteUser userToDelete) {
        SiteUser actor = userContext.getActor();
        if (actor.getUsername().equals("admin")) return;

        if (!userToDelete.getUsername().equals(actor.getUsername())) {
            throw new CustomException(UserErrorCode.PERMISSION_DENIED);
        }
    }

    public void unlockAccount(Long id) {
        SiteUser user = findById(id).get();
        user.unlockAccount();
        userRepository.save(user);
    }

    public String genAccessToken(SiteUser user) {
        return authTokenService.genAccessToken(user);
    }

    public String genAuthToken(SiteUser user) {
        return user.getApiKey() + " " + genAccessToken(user);
    }

    // JWT 로 얻은 가짜 user 객체 (DB 에서 조회한 user 아님)
    public SiteUser getUserFromAccessToken(String accessToken) {
        Map<String, Object> payload = authTokenService.payload(accessToken);

        if (payload == null) return null;

        long id = (long) payload.get("id");
        String username = (String) payload.get("username");
        String nickname = (String) payload.get("nickname");
        Role role = (Role) payload.get("role");

        return new SiteUser(id, username, nickname, role);
    }
}