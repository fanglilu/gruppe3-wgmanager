package msp.gruppe3.wgmanager.ui.uiElements


import android.app.TimePickerDialog
import android.content.Context
import java.util.*

class TimePicker(
    var context: Context,
    var iface: TimePickerInterface?
) : TimePickerDialog.OnTimeSetListener {

    var selectedTime: Calendar = Calendar.getInstance()

    fun showPicker() {

        val timePickerDialog = TimePickerDialog(
            context,
            this,
            selectedTime.get(Calendar.HOUR_OF_DAY),
            selectedTime.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()

    }


    override fun onTimeSet(p0: android.widget.TimePicker?, hour: Int, min: Int) {
        selectedTime.set(Calendar.HOUR_OF_DAY, hour)
        selectedTime.set(Calendar.MINUTE, min)
        iface?.onTimeSelected(selectedTime)
    }

    interface TimePickerInterface {
        fun onTimeSelected(calendar: Calendar)
    }
}