package com.ll.TeamProject.domain.friend.entity

import com.ll.TeamProject.domain.friend.repository.FriendshipRepository
import com.ll.TeamProject.domain.user.entity.SiteUser
import jakarta.persistence.*

@Entity
@Table(
    name = "friendship",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user1_id", "user2_id"])]
)
data class Friendship(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    val user1: SiteUser,

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    val user2: SiteUser,

    @Enumerated(EnumType.STRING)
    var status: FriendshipStatus = FriendshipStatus.PENDING
) {
    companion object {
        // ✅ 친구 요청 생성 (처음에는 PENDING 상태)
        fun create(userA: SiteUser, userB: SiteUser, friendshipRepository: FriendshipRepository): Friendship {
            if (friendshipRepository.existsByUser1AndUser2(userA, userB)) {
                throw IllegalArgumentException("이미 친구 요청을 보냈거나 친구 관계입니다!")
            }
            return if (requireNotNull(userA.id) < requireNotNull(userB.id)) {
                Friendship(user1 = userA, user2 = userB, status = FriendshipStatus.PENDING)
            } else {
                Friendship(user1 = userB, user2 = userA, status = FriendshipStatus.PENDING)
            }
        }
    }

    // ✅ 친구 요청 수락
    fun acceptRequest() {
        if (status == FriendshipStatus.PENDING) {
            status = FriendshipStatus.ACCEPTED
        } else {
            throw IllegalStateException("이미 처리된 요청입니다!")
        }
    }

    // ✅ 친구 요청 거절
    fun declineRequest() {
        if (status == FriendshipStatus.PENDING) {
            status = FriendshipStatus.DECLINED
        } else {
            throw IllegalStateException("이미 처리된 요청입니다!")
        }
    }
}
