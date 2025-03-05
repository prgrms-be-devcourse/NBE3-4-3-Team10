package com.ll.TeamProject.domain.user.service;

import com.ll.TeamProject.domain.user.dto.ForbiddenNicknameList;
import com.ll.TeamProject.domain.user.repository.ForbiddenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ForbiddenService {
    private final ForbiddenRepository forbiddenRepository;

    public boolean isForbidden(String nickname) {
        ForbiddenNicknameList forbiddenList = new ForbiddenNicknameList(forbiddenRepository.findAll());

        return forbiddenList.contains(nickname);
    }
}
