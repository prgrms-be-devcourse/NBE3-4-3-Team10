package com.ll.TeamProject.domain.friend.entity

import com.ll.TeamProject.domain.user.entity.SiteUser
import jakarta.persistence.*

@Entity
@Table(
    name = "friendship",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user1_id", "user2_id"])] // 중복 저장 방지
)
data class Friendship(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,  // JPA가 자동 생성

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    val user1: SiteUser,

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    val user2: SiteUser
) {
    companion object {
        //중복된 userA, userB 들어가는거 방지
        fun create(userA: SiteUser, userB: SiteUser): Friendship {
            return if (requireNotNull(userA.id) < requireNotNull(userB.id)) {
                Friendship(user1 = userA, user2 = userB)
            } else {
                Friendship(user1 = userB, user2 = userA)
            }
        }
    }
}
