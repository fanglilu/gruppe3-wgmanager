package lmu.gruppe3.wgmanager.finance.dto

import lmu.gruppe3.wgmanager.user.dto.ReducedUserDto

data class StatisticByUserDto(
    var currentUser: ReducedUserDto,
    var totalSum: Double,
    var averageAmount: Double,
    var contributed: Double,
    var deviation: Double,
    var expenses: List<ReducedExpenseDto>
)
