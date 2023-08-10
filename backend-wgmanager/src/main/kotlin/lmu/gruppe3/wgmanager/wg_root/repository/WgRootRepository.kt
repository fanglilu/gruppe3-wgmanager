package lmu.gruppe3.wgmanager.wg_root.repository

import lmu.gruppe3.wgmanager.userOnCalendarEntry.domain.UserCalendarKey
import lmu.gruppe3.wgmanager.wg_root.domain.Wg
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface WgRootRepository : JpaRepository<Wg, UUID> {
    fun findByInvitationCode(invitationCode: String): Wg?

    @Query("select w.id from Wg w")
    fun findAllWgIds(): List<UUID>
}