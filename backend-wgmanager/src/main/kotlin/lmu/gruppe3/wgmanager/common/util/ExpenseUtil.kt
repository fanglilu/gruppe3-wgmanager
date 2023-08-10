package lmu.gruppe3.wgmanager.common.util

import kotlin.math.roundToInt

class ExpenseUtil {
    companion object {
        fun convertValidPrice(price: Double): Double {
            return (price * 100.0).roundToInt() / 100.0
        }
    }
}