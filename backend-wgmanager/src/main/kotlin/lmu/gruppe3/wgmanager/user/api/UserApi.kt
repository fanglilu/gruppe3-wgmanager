package lmu.gruppe3.wgmanager.user.api

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lmu.gruppe3.wgmanager.user.dto.RoleUserDto
import lmu.gruppe3.wgmanager.user.dto.UserDto
import lmu.gruppe3.wgmanager.user.service.UserService
import lmu.gruppe3.wgmanager.wg_user.service.WgUserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/user")
class UserApi(
    private val userService: UserService,
    private val wgUserService: WgUserService
) {
    @GetMapping("/all")
    fun getUser(): ResponseEntity<List<UserDto>> {
        val result = this.userService.findAll()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/{wgId}/wg-users")
    fun getUser(@PathVariable wgId: UUID): ResponseEntity<List<RoleUserDto>> {
        val result = this.wgUserService.getWgUsersWithRole(wgId)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/me")
    fun findMe(): ResponseEntity<UserDto> {
        val result = this.userService.getCurrentUser().toDto()
        return ResponseEntity(result, HttpStatus.OK)
    }
}