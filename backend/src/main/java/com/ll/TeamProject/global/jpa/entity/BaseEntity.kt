package com.ll.TeamProject.global.jpa.entity

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import lombok.EqualsAndHashCode

@MappedSuperclass
abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    var id: Long? = null
        protected set  // @Setter(AccessLevel.PROTECTED) 대체

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        // Hibernate Proxy 처리
        val that = other as BaseEntity
        if (this.id == null || that.id == null) return false
        return this.id == that.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}