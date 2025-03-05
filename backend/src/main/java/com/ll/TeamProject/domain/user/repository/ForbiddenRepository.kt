package com.ll.TeamProject.domain.user.repository

import com.ll.TeamProject.domain.user.entity.ForbiddenNickname
import org.springframework.data.jpa.repository.JpaRepository

interface ForbiddenRepository : JpaRepository<ForbiddenNickname, Long>
