package lmu.gruppe3.wgmanager.wg_user.service

import lmu.gruppe3.wgmanager.common.util.AuthUtil
import lmu.gruppe3.wgmanager.user.dto.ReducedUserDto
import lmu.gruppe3.wgmanager.user.dto.RoleUserDto
import lmu.gruppe3.wgmanager.user.enum.UserRole
import lmu.gruppe3.wgmanager.wg_user.repository.WgUserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class WgUserService(
    private val wgUserRepository: WgUserRepository,
) {
    fun getRoleByWgId(wgId: UUID): UserRole {
        val userId = AuthUtil.getCurrentUserId()
        return this.wgUserRepository.findRoleByUserIdAndByWgId(userId, wgId)
    }

    fun getFlatMatesByWgId(wgId: UUID): List<ReducedUserDto> {
        return this.wgUserRepository.findWgUsersByWgId(wgId).map { it.toReducedUserDto() }
    }

    fun getWgUsersWithRole(wgId: UUID): List<RoleUserDto> {
        return this.wgUserRepository.findWgUsersByWgId(wgId).map { it.toUserDto() }
    }
}