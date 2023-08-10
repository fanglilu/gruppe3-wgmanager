package lmu.gruppe3.wgmanager.finance.api

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lmu.gruppe3.wgmanager.finance.dto.AboDto
import lmu.gruppe3.wgmanager.finance.service.AboService
import lmu.gruppe3.wgmanager.wg_root.service.WgValidationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/finance/abo")
class AboApi(
    private val aboService: AboService,
    private val wgValidationService: WgValidationService
) {

    @GetMapping("/by-wgId/{wgId}")
    fun getAllRecurringExpensesByWgId(@PathVariable wgId: UUID): ResponseEntity<List<AboDto>> {
        this.wgValidationService.checkCurrentUserInWg(wgId)
        val result = this.aboService.getAllAboExpensesByWgId(wgId)
        return ResponseEntity(result, HttpStatus.OK)
    }

    /* For testing purposes only */
    @GetMapping("/all")
    fun getAllRecurringExpenses(): ResponseEntity<List<AboDto>> {
        val result = this.aboService.getAllAboExpenses()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @DeleteMapping("/{aboId}")
    fun deleteByAboId(@PathVariable aboId: UUID): ResponseEntity<Any> {
        this.aboService.deleteAboById(aboId)
        return ResponseEntity("Successfully deleted abo (id: $aboId)!", HttpStatus.OK)
    }
}