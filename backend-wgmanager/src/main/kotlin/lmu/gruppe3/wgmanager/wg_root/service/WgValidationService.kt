package lmu.gruppe3.wgmanager.wg_root.service

import lmu.gruppe3.wgmanager.common.util.AuthUtil
import lmu.gruppe3.wgmanager.wg_root.repository.WgRootRepository
import lmu.gruppe3.wgmanager.wg_user.domain.WgUserKey
import lmu.gruppe3.wgmanager.wg_user.repository.WgUserRepository
import org.springframework.stereotype.Service
import java.security.InvalidParameterException
import java.util.*

@Service
class WgValidationService(
    private val wgRootRepository: WgRootRepository,
    private val wgUserRepository: WgUserRepository
) {

    fun checkValidWgId(wgId: UUID) {
        if (!this.wgRootRepository.existsById(wgId)) {
            throw InvalidParameterException("Invalid wgId: $wgId !")
        }
    }

    fun checkCurrentUserInWg(wgId: UUID) {
        this.checkValidWgId(wgId)
        val currentUserId = AuthUtil.getCurrentUserId()
        val wgUserKey = WgUserKey(
            userId = currentUserId,
            wgId = wgId
        )

        if (!this.wgUserRepository.existsById(wgUserKey)) {
            throw IllegalAccessException("User $currentUserId is not allowed to access Wg $wgId!")
        }
    }
}