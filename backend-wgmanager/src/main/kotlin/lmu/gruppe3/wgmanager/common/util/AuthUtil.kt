package lmu.gruppe3.wgmanager.common.util

import lmu.gruppe3.wgmanager.auth.dto.PrincipalDto
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

class AuthUtil {
    companion object {
        fun getPrincipal(): PrincipalDto {
            return SecurityContextHolder.getContext().authentication.principal as PrincipalDto
        }

        fun getCurrentUserId(): UUID {
            return this.getPrincipal().userId
        }
    }

}