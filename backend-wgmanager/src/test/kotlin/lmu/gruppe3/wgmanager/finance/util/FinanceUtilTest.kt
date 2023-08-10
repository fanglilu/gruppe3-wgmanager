package lmu.gruppe3.wgmanager.finance.util

import lmu.gruppe3.wgmanager.finance.dto.DeviationDto
import lmu.gruppe3.wgmanager.user.dto.ReducedUserDto
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

internal class FinanceUtilTest {
    private val testUser = ReducedUserDto(UUID.randomUUID(), "test")

    private val input1 = listOf(
        DeviationDto(testUser, BigDecimal.valueOf(80.99)),
        DeviationDto(testUser, BigDecimal.valueOf(29.01)),
        DeviationDto(testUser, BigDecimal.valueOf(-23.55)),
        DeviationDto(testUser, BigDecimal.valueOf(-17.02)),
        DeviationDto(testUser, BigDecimal.valueOf(-69.43)),
    )

    @Test
    fun splitDebtorsAndReceivers() {
        val result = FinanceUtil.splitDebtorsAndReceivers(input1)
    }

    @Test
    fun filterOutPayedDeviations() {
        val deviations = FinanceUtil.splitDebtorsAndReceivers(input1)
        val receivers = deviations[0]
        val debtors = deviations[1]

        val debts = FinanceUtil.createNewDebts(receivers, debtors)
        val receiverSum = receivers.sumOf { it.deviation }
        val debtSum = debts.sumOf { it.debt }
        val result = receiverSum.toDouble() == debtSum
        assert(result).equals(true)
    }


    @Test
    fun test() {
        val num1 = BigDecimal("18.0")
        val num2 = BigDecimal("-18")
        val result = num1 == num2
        val result2 = num1 + num2
        assert(true).equals(true)
    }
}