package lmu.gruppe3.wgmanager.user.service

import lmu.gruppe3.wgmanager.common.util.AuthUtil
import lmu.gruppe3.wgmanager.common.util.ValidatorUtil
import lmu.gruppe3.wgmanager.user.domain.User
import lmu.gruppe3.wgmanager.user.dto.RegisterUserDto
import lmu.gruppe3.wgmanager.user.dto.UserDto
import lmu.gruppe3.wgmanager.user.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.security.InvalidParameterException
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    var logger: Logger = LoggerFactory.getLogger(UserService::class.java)
    fun findAll(): List<UserDto> {
        return this.userRepository.findAll().map(User::toDto)
    }

    fun findByEmail(email: String): UserDto {
        if (!ValidatorUtil.isValidEmail(email)) {
            throw InvalidParameterException("invalid email")
        }
        val user = this.userRepository.findByEmail(email) ?: throw InvalidParameterException("user not found")
        return user.toDto()
    }

    fun findById(id: UUID): User {
        return userRepository.findById(id).orElseThrow { InvalidParameterException("user not found") }
    }

    fun getCurrentUser(): User {
        return this.findById(AuthUtil.getCurrentUserId())
    }

    fun createUser(userDto: RegisterUserDto): UserDto {
        if (!ValidatorUtil.isValidEmail(userDto.email)) {
            throw InvalidParameterException("invalid email")
        }
        val newUser = User(
            email = userDto.email,
            name = userDto.name,
            password = BCryptPasswordEncoder().encode(userDto.password)
        )
        val savedUser = this.userRepository.saveAndFlush(newUser)
        this.logger.info("user ${newUser.id} created")
        return savedUser.toDto()
    }


}