package lmu.gruppe3.wgmanager.wg_root.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import lmu.gruppe3.wgmanager.common.domain.BaseEntity
import lmu.gruppe3.wgmanager.feature.domain.Feature
import lmu.gruppe3.wgmanager.user.enum.UserRole
import lmu.gruppe3.wgmanager.wg_root.dto.RoleWgDto
import lmu.gruppe3.wgmanager.wg_root.dto.WgDto
import lmu.gruppe3.wgmanager.wg_user.domain.WgUser
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "wg")
data class Wg(
    @Column(unique = true)
    var invitationCode: String,
    var invitationCodeExpires: LocalDateTime,
    var name: String,
) : BaseEntity() {
    @OneToMany(mappedBy = "wg", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    val wgUserList: MutableSet<WgUser> = mutableSetOf()
    @OneToMany(mappedBy = "wg", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    val features: MutableSet<Feature> = mutableSetOf()

    fun toDto(): WgDto {
        return WgDto(
            id = this.id,
            name = this.name,
            features = emptyList(),
            invitationCode = this.invitationCode,
            createdDate = this.created,
            userList = this.wgUserList.map { it.toUserDto() }.toMutableList()
        )
    }

    fun toRoleDto(role: UserRole): RoleWgDto {
        return RoleWgDto(
            id = this.id,
            name = this.name,
            invitationCode = this.invitationCode,
            joinedAt = this.wgUserList.maxByOrNull { it.joinedAt }!!.joinedAt,
            role = role
        )
    }
}