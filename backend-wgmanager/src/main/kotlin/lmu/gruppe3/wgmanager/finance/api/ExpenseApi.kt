package lmu.gruppe3.wgmanager.finance.api

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lmu.gruppe3.wgmanager.common.dto.VoidRequestResponse
import lmu.gruppe3.wgmanager.finance.dto.ExpenseDto
import lmu.gruppe3.wgmanager.finance.dto.InvoicePeriodWithExpensesDto
import lmu.gruppe3.wgmanager.finance.dto.InvoiceStatusByUserDto
import lmu.gruppe3.wgmanager.finance.dto.StatisticByUserDto
import lmu.gruppe3.wgmanager.finance.service.ExpenseService
import lmu.gruppe3.wgmanager.wg_root.service.WgValidationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/finance/expense")
class ExpenseApi(
    private val expenseService: ExpenseService,
    private val wgValidationService: WgValidationService
) {

    // get one expense by its id to view in detail (image included)
    @GetMapping("/{expenseId}")
    fun getExpenseById(@PathVariable expenseId: UUID): ResponseEntity<ExpenseDto> {
        val result = this.expenseService.findById(expenseId).toDto()
        return ResponseEntity(result, HttpStatus.OK)
    }

    /*
        * get expenses by period id, if given, else expenses of the current period of given wg
        * used to browse different periods and see expenses and general statistics
     */
    @GetMapping("/{wgId}/expenses-by-periodId")
    fun getExpensesByPeriodId(
        @PathVariable wgId: UUID,
        @RequestParam(required = false) periodId: UUID?
    ): ResponseEntity<InvoicePeriodWithExpensesDto> {
        this.wgValidationService.checkCurrentUserInWg(wgId)
        val result = this.expenseService.findExpensesByPeriodId(wgId, periodId)
        return ResponseEntity(result, HttpStatus.OK)
    }

    /*
        * CURRENTLY NOT USED
        * list of all periods with expenses of a wg
        * pagination should be included
        * not preferred
     */
    @GetMapping("/{wgId}/all-periods")
    fun getAllPeriodsByWgId(@PathVariable wgId: UUID): ResponseEntity<List<InvoicePeriodWithExpensesDto>> {
        this.wgValidationService.checkCurrentUserInWg(wgId)
        val result = this.expenseService.findExpensesOfAllPeriods(wgId)
        return ResponseEntity(result, HttpStatus.OK)
    }

    /*
        * general statistic of all expenses ever of a wg
        * point of view: current user
        * pagination should be included
     */

    @GetMapping("/{wgId}/status-all-expenses")
    fun getMyStatusOfAllExpenses(@PathVariable wgId: UUID): ResponseEntity<StatisticByUserDto> {
        this.wgValidationService.checkCurrentUserInWg(wgId)
        val result = this.expenseService.findAllExpensesOfCurrentUserByWgId(wgId)
        return ResponseEntity(result, HttpStatus.OK)
    }

    /*
        * general information (average, contributed etc) of period by periodId
        * point of view: current user
     */
    @GetMapping("/status/{periodId}")
    fun getStatusByPeriodId(@PathVariable periodId: UUID): ResponseEntity<InvoiceStatusByUserDto> {
        val result = this.expenseService.calculateTotalExpenseByUser(periodId)
        return ResponseEntity(result, HttpStatus.OK)
    }

    /*
        * CURRENTLY NOT USED
        * list of all periods with general information
        * intend to use as period selector/ overview of all perios of a wg
     */
    @GetMapping("/all-status/{periodId}")
    fun getAllStatusByPeriodId(@PathVariable periodId: UUID): ResponseEntity<List<InvoiceStatusByUserDto>> {
        val result = this.expenseService.calculateTotalExpensesOfAll(periodId)
        return ResponseEntity(result, HttpStatus.OK)
    }

    /*
        * create an expense for given wg and current period
        * required: description, price
        * additional: recurring, image
     */
    @PostMapping("/{wgId}/create")
    fun createExpense(
        @RequestBody expense: ExpenseDto,
        @PathVariable wgId: UUID
    ): ResponseEntity<ExpenseDto> {
        this.wgValidationService.checkCurrentUserInWg(wgId)
        val result = this.expenseService.createExpense(expense, wgId)
        return ResponseEntity(result, HttpStatus.OK)
    }

    /*
        * update an expense for given wg
        * forFuture: if true abo is also updated
        * user validation to edit item of wg and period
     */
    @PostMapping("/{wgId}/update")
    fun updateExpense(
        @RequestBody expense: ExpenseDto,
        @PathVariable wgId: UUID,
        @RequestParam(required = false) forFuture: Boolean?
    ): ResponseEntity<ExpenseDto> {
        this.wgValidationService.checkCurrentUserInWg(wgId)
        val result = this.expenseService.updateExpense(expense, wgId, forFuture)
        return ResponseEntity(result, HttpStatus.OK)
    }

    /*
        * delete an expense for given wg
        * forFuture: if true abo is also updated
        * user validation to edit item of wg and period
     */
    @DeleteMapping("/{wgId}/{expenseId}")
    fun deleteExpenseById(
        @PathVariable expenseId: UUID,
        @PathVariable wgId: UUID,
        @RequestParam(required = false) forFuture: Boolean
    ): ResponseEntity<VoidRequestResponse> {
        this.wgValidationService.checkCurrentUserInWg(wgId)
        this.expenseService.deleteExpenseById(expenseId, wgId, forFuture)
        val result = VoidRequestResponse(true)
        return ResponseEntity(result, HttpStatus.OK)
    }

    /*
        * For testing purposes on swagger to upload an image of saved expense
     */
    @PostMapping("{expenseId}/upload-file", consumes = ["multipart/form-data"])
    fun uploadFile(
        @RequestPart(required = true) uploadedFile: MultipartFile,
        @PathVariable expenseId: UUID
    ): ResponseEntity<Any> {
        this.expenseService.uploadFile(uploadedFile, expenseId)
        return ResponseEntity("It worked!", HttpStatus.OK)
    }
}