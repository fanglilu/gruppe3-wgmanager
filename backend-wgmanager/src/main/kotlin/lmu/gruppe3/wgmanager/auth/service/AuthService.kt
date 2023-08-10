package lmu.gruppe3.wgmanager.auth.service

import lmu.gruppe3.wgmanager.auth.dto.LoginDto
import lmu.gruppe3.wgmanager.common.jwt.JwtUtil
import lmu.gruppe3.wgmanager.common.util.ValidatorUtil
import lmu.gruppe3.wgmanager.user.repository.UserRepository
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(private val userRepository: UserRepository, private val jwtUtil: JwtUtil) {

    fun login(loginDto: LoginDto): String {
        println("try login ${loginDto.email}")
        if (!ValidatorUtil.isValidEmail(loginDto.email)) {
            throw BadCredentialsException("invalid email")
        }
        val user = this.userRepository.findByEmail(loginDto.email) ?: throw BadCredentialsException("Login failed")
        if (!BCryptPasswordEncoder().matches(loginDto.password, user.password)) {
            throw BadCredentialsException("Wrong password")
        }
        return this.jwtUtil.generateJwtToken(user.toDto()) ?: throw Exception("Login failed")
    }

}