package lmu.gruppe3.wgmanager.auth.api

import lmu.gruppe3.wgmanager.auth.dto.LoginDto
import lmu.gruppe3.wgmanager.auth.dto.TokenDto
import lmu.gruppe3.wgmanager.auth.service.AuthService
import lmu.gruppe3.wgmanager.user.dto.RegisterUserDto
import lmu.gruppe3.wgmanager.user.dto.UserDto
import lmu.gruppe3.wgmanager.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthApi(private val authService: AuthService, private val userService: UserService) {

    @PostMapping("/login")
    fun login(@RequestBody loginDto: LoginDto): ResponseEntity<TokenDto> {
        val result = this.authService.login(loginDto)
        return ResponseEntity(TokenDto(result), HttpStatus.ACCEPTED)
    }
    
    @PostMapping("/register")
    fun registerUser(@RequestBody registerUserDto: RegisterUserDto): ResponseEntity<UserDto> {
        val result = this.userService.createUser(registerUserDto)
        return ResponseEntity(result, HttpStatus.OK)
    }

}