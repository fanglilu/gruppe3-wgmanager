package msp.gruppe3.wgmanager.ui.uiElements

import android.content.Context
import android.widget.Button
import android.widget.TextView
import msp.gruppe3.wgmanager.common.DateConversionUtil
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateTimePickersWithStartAndEndViewInitializer(
    private val context: Context,
    var startDate: LocalDate,
    var endDate: LocalDate,
    var startTime: LocalTime,
    var endTime: LocalTime
) {


    fun initDatePicker(
        startButton: TextView,
        endButton: TextView,
        startDateView: TextView,
        endDateView: TextView,
        startTimeButton: TextView,
        endTimeButton: TextView,
        startTimeView: TextView,
        endTimeView: TextView
    ) {

        val selectStartDate = startButton.setOnClickListener {
            val datePicker = DatePicker(
                this.context,
                object : DatePicker.DateTimePickerInterface {
                    override fun onDateTimeSelected(calendar: Calendar) {
                        startDate =
                            DateConversionUtil().convertCalendarToLocalTime(calendar).toLocalDate()


                        startDateView.text = startDate.toString()
                        endDateView.hint = startDate.toString()
                        if (endDateView.text.isNotBlank()) {
                            if (startDate.isAfter(endDate)) {
                                endDate = startDate
                                if(endTime.isBefore(startTime)){
                                    startTime = endTime
                                }
                            }
                        }
                        callTimePicker(true, startTimeView, endTimeView)


                    }
                }
            ,
            calendar = DateConversionUtil().convertLocalTimeToCalendar(startDate))
            datePicker.showDatePicker()
        }

        val selectEndDate = endButton.setOnClickListener {
            val datePicker = DatePicker(
                this.context,
                object : DatePicker.DateTimePickerInterface {
                    override fun onDateTimeSelected(calendar: Calendar) {
                        endDate =
                            DateConversionUtil().convertCalendarToLocalTime(calendar).toLocalDate()
                        if (startDate.isAfter(endDate)) {
                            startDateView.text = endDate.toString()
                            if(startTime.isAfter(endTime)){
                                endTime = startTime
                            }
                        }
                        endDateView.text = endDate.toString()
                        callTimePicker(false, startTimeView, endTimeView)
                    }
                } ,
                calendar = DateConversionUtil().convertLocalTimeToCalendar(endDate)
            )

            datePicker.showDatePicker()
        }
    }

    fun initTimePicker(
        startTimeButton: TextView,
        endTimeButton: TextView,
        startTimeView: TextView,
        endTimeView: TextView
    ) {
        val selectStartTime = startTimeButton.setOnClickListener {
            callTimePicker(true, startTimeView, endTimeView)
        }
        val selectEndTime = endTimeButton.setOnClickListener {
            callTimePicker(false, startTimeView, endTimeView)
        }
    }


    private fun callTimePicker(
        isStartDate: Boolean,
        startTimeView: TextView,
        endTimeView: TextView
    ) {
        val timePicker = TimePicker(
            this.context,
            object : TimePicker.TimePickerInterface {
                override fun onTimeSelected(calendar: Calendar) {
                    if (isStartDate) {
                        startTime =
                            DateConversionUtil()
                                .convertCalendarToLocalTime(calendar)
                                .toLocalTime()
                        if (startTime.isAfter(endTime) && endDate.isEqual(startDate)) {
                            endTime = startTime
                            endTimeView.text =
                                endTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString()
                        }
                        startTimeView.text =
                            startTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString()
                    } else {
                        endTime =
                            DateConversionUtil()
                                .convertCalendarToLocalTime(calendar)
                                .toLocalTime()
                        if (endTime.isBefore(startTime) && endDate.isEqual(startDate)) {
                            startTime = endTime
                            startTimeView.text =
                                startTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString()
                        }
                        endTimeView.text =
                            endTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString()

                    }
                }
            }
        )
        timePicker.showPicker()
    }
}