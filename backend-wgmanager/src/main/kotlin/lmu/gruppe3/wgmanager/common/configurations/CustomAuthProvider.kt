package lmu.gruppe3.wgmanager.common.configurations

import lmu.gruppe3.wgmanager.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component
class CustomAuthProvider : AuthenticationProvider {
    @Autowired
    private val userRepository: UserRepository? = null

    override fun authenticate(authentication: Authentication): Authentication {
        val loginEmail = authentication.name
        val loginPassword = authentication.credentials.toString()
        val user = this.userRepository?.findByEmail(loginEmail) ?: throw BadCredentialsException("Login failed")

        if (!BCryptPasswordEncoder().matches(loginPassword, user.password)) {
            throw BadCredentialsException("Wrong password")
        }
        return UsernamePasswordAuthenticationToken(loginEmail, loginPassword, Collections.emptyList())
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}