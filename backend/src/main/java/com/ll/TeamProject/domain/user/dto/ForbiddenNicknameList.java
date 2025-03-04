package com.ll.TeamProject.domain.user.dto;

import com.ll.TeamProject.domain.user.entity.ForbiddenNickname;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ForbiddenNicknameList {
    private final Set<String> forbiddenList;

    public ForbiddenNicknameList(List<ForbiddenNickname> forbiddenNames) {
        this.forbiddenList = forbiddenNames.stream().map(fn -> fn.getForbiddenName().toLowerCase())
                .collect(Collectors.toSet());
    }

    public boolean contains(String nickname) {
        return forbiddenList.stream().anyMatch(nickname.toLowerCase()::contains);
    }
}
