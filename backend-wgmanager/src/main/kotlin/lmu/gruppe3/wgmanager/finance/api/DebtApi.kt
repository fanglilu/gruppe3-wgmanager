package lmu.gruppe3.wgmanager.finance.api

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lmu.gruppe3.wgmanager.common.dto.VoidRequestResponse
import lmu.gruppe3.wgmanager.finance.dto.DebtDto
import lmu.gruppe3.wgmanager.finance.dto.DebtKeyDto
import lmu.gruppe3.wgmanager.finance.dto.ReducedDebtDto
import lmu.gruppe3.wgmanager.finance.service.DebtService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/finance/debt")
class DebtApi(private val debtService: DebtService) {

    @GetMapping("/{periodId}")
    fun getDebtsByPeriodId(@PathVariable periodId: UUID): ResponseEntity<List<ReducedDebtDto>> {
        val result = this.debtService.getDebtsByPeriodId(periodId)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/{periodId}/me")
    fun getMyDebtsByPeriodId(@PathVariable periodId: UUID): ResponseEntity<List<ReducedDebtDto>> {
        val result = this.debtService.getMyDebtsByPeriodId(periodId)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/{wgId}/unsettled-debts")
    fun getAllMyUnsettledDebtsByWgId(@PathVariable wgId: UUID): ResponseEntity<List<DebtDto>> {
        val result = this.debtService.getMyUnsettledDebtsByWgId(wgId)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @PostMapping("/{periodId}/settled")
    fun settleDebt(
        @PathVariable periodId: UUID,
        @RequestBody debt: ReducedDebtDto
    ): ResponseEntity<ReducedDebtDto> {
        val result = this.debtService.settleDebt(periodId, debt)
        return ResponseEntity(result, HttpStatus.OK)
    }

    /*
        * send list of debts (DebtKeys) which are submitted as settled
     */
    @PostMapping("/multiple-settled")
    fun settleMultipleDebts(
        @RequestBody debts: List<DebtKeyDto>,
    ): ResponseEntity<List<DebtDto>> {
        val result = this.debtService.settleMultipleDebts(debts)
        return ResponseEntity(result, HttpStatus.OK)
    }

    // Nice To Have: WIP for independent settle ups
    @GetMapping("/{receiverId}/{periodId}")
    fun createDebt(
        @PathVariable receiverId: UUID,
        @PathVariable periodId: UUID
    ): ResponseEntity<DebtDto> {
        val result = this.debtService.createDebt(receiverId, periodId)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/{periodId}/cash-crash")
    fun cashCrashByPeriodId(@PathVariable periodId: UUID): ResponseEntity<VoidRequestResponse> {
        this.debtService.cashCrashByPeriodId(periodId)
        val result = VoidRequestResponse(true)
        return ResponseEntity(result, HttpStatus.OK)
    }
}