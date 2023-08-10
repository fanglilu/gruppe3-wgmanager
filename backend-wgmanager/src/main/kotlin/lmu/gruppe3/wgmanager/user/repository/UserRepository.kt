package lmu.gruppe3.wgmanager.user.repository

import lmu.gruppe3.wgmanager.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {

    fun findByEmail(email: String): User?
}