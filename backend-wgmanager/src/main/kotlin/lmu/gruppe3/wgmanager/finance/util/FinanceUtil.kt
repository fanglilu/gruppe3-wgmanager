package lmu.gruppe3.wgmanager.finance.util

import lmu.gruppe3.wgmanager.common.util.ExpenseUtil
import lmu.gruppe3.wgmanager.finance.dto.DeviationDto
import lmu.gruppe3.wgmanager.finance.dto.ReducedDebtDto
import java.math.BigDecimal
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidParameterException

class FinanceUtil {

    companion object {
        private val zero: BigDecimal = BigDecimal.valueOf(0.0)

        fun splitDebtorsAndReceivers(deviations: List<DeviationDto>): List<List<DeviationDto>> {
            val sortedDeviation = deviations.sortedBy { it.deviation }
            val receivers = sortedDeviation.filter { it.deviation > zero }.reversed()
            val debtors = sortedDeviation.filter { it.deviation < zero }
            return listOf(receivers, debtors)
        }

        fun createNewDebts(
            recs: List<DeviationDto>,
            debs: List<DeviationDto>
        ): List<ReducedDebtDto> {
            val debts = mutableListOf<ReducedDebtDto>()
            val receivers = recs.map { it.copy() }.toMutableList()
            val debtors = debs.map { it.copy() }.toMutableList()
            receivers.forEach { receiver ->
                while (receiver.deviation > zero) {
                    if (debtors.sumOf { it.deviation } == zero) {
                        break
                    }
                    for (debtor in debtors) {
                        var debtVal: Double
                        when {
                            receiver.deviation == zero -> break
                            debtor.deviation == zero -> continue

                            receiver.deviation.abs() > debtor.deviation.abs() -> {
                                debtVal = debtor.deviation.abs().toDouble()
                                receiver.deviation = receiver.deviation + debtor.deviation
                                debtor.deviation = zero
                            }

                            receiver.deviation.abs() < debtor.deviation.abs() -> {
                                debtVal = receiver.deviation.abs().toDouble()
                                debtor.deviation = debtor.deviation + receiver.deviation
                                receiver.deviation = zero
                            }

                            else -> {
                                debtVal = receiver.deviation.abs().toDouble()
                                receiver.deviation = zero
                                debtor.deviation = zero
                            }
                        }

                        debts.add(
                            ReducedDebtDto(
                                receiver.user,
                                debtor.user,
                                ExpenseUtil.convertValidPrice(debtVal),
                                false
                            )
                        )
                    }
                }
            }
            return debts
        }

        fun validateDebts(receivers: List<DeviationDto>, debts: List<ReducedDebtDto>) {
            val receiverSum = ExpenseUtil.convertValidPrice(receivers.sumOf { it.deviation }.toDouble())
            val debtSum = debts.sumOf { it.debt }
            if (receiverSum != debtSum) {
                throw InvalidParameterException("Overpaid sum ($receiverSum) does not tally with debt sum ($debtSum)!")
            }
        }


        fun validateDeviations(deviations: List<DeviationDto>) {
            val deviationSum = ExpenseUtil.convertValidPrice(deviations.sumOf { it.deviation.toDouble() })
            if (deviationSum != 0.0) {
                throw InvalidAlgorithmParameterException("Sum of deviations = $deviationSum, therefor do not balance out!")
            }
        }
    }
}