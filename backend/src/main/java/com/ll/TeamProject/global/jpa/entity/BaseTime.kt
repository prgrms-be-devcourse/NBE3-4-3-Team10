package com.ll.TeamProject.global.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTime : BaseEntity() {

    @CreatedDate
    @Column(updatable = false)
    final var createDate: LocalDateTime? = null
        private set

    @LastModifiedDate
    final var modifyDate: LocalDateTime? = null
        private set
}
