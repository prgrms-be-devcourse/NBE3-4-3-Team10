package com.ll.TeamProject.domain.user.entity

import com.ll.TeamProject.global.jpa.entity.BaseTime
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
class ForbiddenNickname(
    @Column(unique = true)
    var forbiddenName: String
) : BaseTime() {

    protected constructor() : this("")
}
