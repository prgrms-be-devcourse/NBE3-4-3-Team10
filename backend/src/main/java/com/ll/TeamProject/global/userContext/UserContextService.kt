package com.ll.TeamProject.global.userContext;

import com.ll.TeamProject.domain.user.entity.SiteUser;
import com.ll.TeamProject.global.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserContextService {

    private final UserContext userContext;

    public SiteUser getAuthenticatedUser() {
        return userContext.findActor()
                .orElseThrow(() -> new ServiceException("401", "로그인을 먼저 해주세요!"));
    }
}
