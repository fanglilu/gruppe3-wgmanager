package lmu.gruppe3.wgmanager.wg_user.repository

import lmu.gruppe3.wgmanager.user.enum.UserRole
import lmu.gruppe3.wgmanager.wg_user.domain.WgUser
import lmu.gruppe3.wgmanager.wg_user.domain.WgUserKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface WgUserRepository : JpaRepository<WgUser, WgUserKey> {
    @Query("select w.role from WgUser w where w.user.id = :userId and w.wg.id = :wgId")
    fun findRoleByUserIdAndByWgId(@Param("userId") userId: UUID, @Param("wgId") wgId: UUID): UserRole

    fun findWgUsersByWgId(wg_id: UUID): List<WgUser>
}