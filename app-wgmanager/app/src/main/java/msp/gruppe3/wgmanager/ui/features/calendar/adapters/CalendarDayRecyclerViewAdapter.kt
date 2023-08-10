package msp.gruppe3.wgmanager.ui.features.calendar.adapters

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.databinding.DayEntriesLayoutBinding
import msp.gruppe3.wgmanager.models.CalendarEntry


class CalendarDayRecyclerViewAdapter :
    RecyclerView.Adapter<CalendarDayRecyclerViewAdapter.MyViewHolder>() {
    var dayEntries: MutableList<CalendarEntry> = mutableListOf<CalendarEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            DayEntriesLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dayEntries[position]
        holder.text.text = currentItem.title
        holder.startDate.text = currentItem.date
        holder.endDate.text = currentItem.endingDate
        holder.text.setBackgroundColor(Color.WHITE)
        holder.text.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("calendarId", currentItem.id.toString())
            bundle.putString("userId", currentItem.creator.toString())
            Navigation.findNavController(holder.itemView)
                .navigate(R.id.calendarDetailFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return dayEntries.size
    }

    class MyViewHolder(val binding: DayEntriesLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val text: TextView = binding.calendarEntryDate
        val startDate: TextView = binding.startDateDisplay
        val endDate: TextView = binding.endDateDisplay
    }
}