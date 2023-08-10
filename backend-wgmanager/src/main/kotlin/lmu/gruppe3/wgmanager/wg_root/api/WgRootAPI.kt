package lmu.gruppe3.wgmanager.wg_root.api

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lmu.gruppe3.wgmanager.common.dto.VoidRequestResponse
import lmu.gruppe3.wgmanager.wg_root.dto.InvitationCodeDto
import lmu.gruppe3.wgmanager.wg_root.dto.WgCreateDto
import lmu.gruppe3.wgmanager.wg_root.dto.WgDto
import lmu.gruppe3.wgmanager.wg_root.service.WgRootService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/wg")
class WgRootAPI(private val wgRootService: WgRootService) {

    @GetMapping()
    fun getAllWg(): ResponseEntity<List<WgDto>> {
        val result = this.wgRootService.getAll()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getWg(@PathVariable("id") id: UUID): ResponseEntity<WgDto> {
        val result = this.wgRootService.findWgById(id)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/{id}/code")
    fun invitationCodeWg(@PathVariable("id") id: UUID): ResponseEntity<InvitationCodeDto> {
        val result = this.wgRootService.getInvitationCode(id)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @PostMapping("/create")
    fun createWg(@RequestBody wgCreateDto: WgCreateDto): ResponseEntity<WgDto> {
        val result = this.wgRootService.createWg(wgCreateDto)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/join")
    fun joinWg(@RequestParam code: String): ResponseEntity<WgDto> {
        val result = this.wgRootService.joinWg(code)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @DeleteMapping("leaveWg/{id}")
    fun leaveWg(@PathVariable id: UUID): ResponseEntity<VoidRequestResponse> {
        val result = this.wgRootService.leaveWg(id)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @DeleteMapping("{id}")
    fun deleteWg(@PathVariable id: UUID): ResponseEntity<VoidRequestResponse> {
        val result = this.wgRootService.deleteWg(id)
        return ResponseEntity(result, HttpStatus.OK)
    }
}