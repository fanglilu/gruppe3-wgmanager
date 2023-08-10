package lmu.gruppe3.wgmanager.wg_user.domain

import lmu.gruppe3.wgmanager.user.domain.User
import lmu.gruppe3.wgmanager.user.dto.ReducedUserDto
import lmu.gruppe3.wgmanager.user.dto.RoleUserDto
import lmu.gruppe3.wgmanager.user.enum.UserRole
import lmu.gruppe3.wgmanager.wg_root.domain.Wg
import lmu.gruppe3.wgmanager.wg_root.dto.RoleWgDto
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class WgUser(
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("wgId")
    @JoinColumn(name = "wg_id")
    val wg: Wg,

    @Enumerated(EnumType.STRING)
    val role: UserRole,

    val joinedAt: LocalDateTime
) {
    @EmbeddedId
    private var id: WgUserKey = WgUserKey(this.user.id, this.wg.id)

    fun toUserDto(): RoleUserDto {
        return this.user.toRoleDto(this.role)
    }

    fun toReducedUserDto(): ReducedUserDto {
        return this.user.toReducedDto()
    }

    fun toWgDto(): RoleWgDto {
        return this.wg.toRoleDto(this.role)
    }

}
