package lmu.gruppe3.wgmanager.user.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import lmu.gruppe3.wgmanager.common.domain.BaseEntity
import lmu.gruppe3.wgmanager.user.dto.ReducedUserDto
import lmu.gruppe3.wgmanager.user.dto.RoleUserDto
import lmu.gruppe3.wgmanager.user.dto.UserDto
import lmu.gruppe3.wgmanager.user.dto.UserIdDto
import lmu.gruppe3.wgmanager.user.enum.UserRole
import lmu.gruppe3.wgmanager.wg_user.domain.WgUser
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Column(unique = true)
    var email: String,
    var name: String,
    var password: String,
) : BaseEntity() {

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    val wgUserList: MutableSet<WgUser> = mutableSetOf()


    fun toDto(): UserDto {
        return UserDto(
            id = this.id,
            name = this.name,
            email = this.email,
            wgList = this.wgUserList.map { it.toWgDto() }.toMutableList()
        )
    }

    fun toRoleDto(role: UserRole): RoleUserDto {
        return RoleUserDto(
            id = this.id,
            name = this.name,
            email = this.email,
            role = role
        )
    }

    fun toUserIdDto(): UserIdDto {
        return UserIdDto(
            id = this.id,
        )
    }

    fun toReducedUserDto(): ReducedUserDto{
        return ReducedUserDto(
            id = this.id,
            name = this.name
        )
    }

    fun toReducedDto(): ReducedUserDto {
        return ReducedUserDto(
            id = this.id,
            name = this.name
        )
    }

}
