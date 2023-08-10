package msp.gruppe3.wgmanager.common

import msp.gruppe3.wgmanager.models.dtos.finance.InvoicePeriodDto
import java.time.format.DateTimeFormatter

private const val DATE_PATTERN = "dd.MM.yyyy"

class StringBuilderUtil {
    companion object {
        /**
         * @return period string for UI ("dd.MM.yyyy") - ("dd.MM.yyyy") || ("dd.MM.yyyy") - now if no endDate provided
         */
        fun buildPeriodString(invoicePeriod: InvoicePeriodDto): String {
            val dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
            var periodDateString = invoicePeriod.startDate.format(dateFormatter).toString()
            periodDateString += if (invoicePeriod.endDate != null) {
                " - ${invoicePeriod.endDate?.format(dateFormatter)}"
            } else {
                " - now"
            }
            return periodDateString
        }

        /**
         * @return formatted date string ("dd.MM.yyyy")
         */
        fun formatDate(date: String): String {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
            val dateAsDate = dateFormatter.parse(date)
            val newDate = DateTimeFormatter.ofPattern(DATE_PATTERN).format(dateAsDate)
            return newDate
        }
    }
}