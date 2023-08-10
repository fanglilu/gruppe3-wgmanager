package msp.gruppe3.wgmanager.ui.uiElements

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

class DatePicker(var context: Context, var iface: DateTimePickerInterface?, val calendar: Calendar) :
    DatePickerDialog.OnDateSetListener {

    val selectedDate = calendar
    fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            context,
            this,
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onDateSet(p0: android.widget.DatePicker?, year: Int, month: Int, day: Int) {
        selectedDate.set(Calendar.YEAR, year)
        selectedDate.set(Calendar.MONTH, month)
        selectedDate.set(Calendar.DAY_OF_MONTH, day)
        iface?.onDateTimeSelected(selectedDate)
    }


    interface DateTimePickerInterface {
        fun onDateTimeSelected(calendar: Calendar)
    }


}