package msp.gruppe3.wgmanager.ui.features.calendar.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import msp.gruppe3.wgmanager.MainViewModel
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.databinding.CalendarDayLayoutBinding
import msp.gruppe3.wgmanager.databinding.FragmentCalendarHomeBinding
import msp.gruppe3.wgmanager.models.CalendarEntry
import msp.gruppe3.wgmanager.models.Wg
import msp.gruppe3.wgmanager.services.CalendarService
import msp.gruppe3.wgmanager.ui.features.calendar.CalendarViewModel
import msp.gruppe3.wgmanager.ui.features.calendar.adapters.CalendarDayRecyclerViewAdapter
import retrofit2.HttpException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*

class CalendarHomeFragment : Fragment() {
    private var _binding: FragmentCalendarHomeBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var calendarDayRecyclerViewAdapter: CalendarDayRecyclerViewAdapter
    private lateinit var calendarService: CalendarService
    private lateinit var calendarViewModel: CalendarViewModel

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    private lateinit var wgId: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val view = binding.root
        findNavController().popBackStack(R.id.calendarDetailFragment, true)

        val createNewCalendarEntryButton = binding.createNewCalendarEntryButton.setOnClickListener {
            val bundle = Bundle()
            if (selectedDate != null) {
                bundle.putString("StartDate", selectedDate.toString())
                findNavController().navigate(R.id.calendarAddNewEventFragment, bundle)
            }
        }
        calendarViewModel = CalendarViewModel()
        calendarService = CalendarService(TokenUtil.getTokenByActivity(requireActivity()))
        addMultiDaySpanningEventsToMap()
        val calendarObserver =
            androidx.lifecycle.Observer<Map<LocalDate, MutableList<CalendarEntry>>> {

                setUpCalendar(binding.calendarView, it)
                selectedDate = today
                updateAdapterForDate(selectedDate, it)
            }
        calendarViewModel.getCalendarDataMapModified().observe(viewLifecycleOwner, calendarObserver)
        return view
    }

    private fun setUpCalendar(
        calendarView: CalendarView,
        calendarMap: Map<LocalDate, MutableList<CalendarEntry>>
    ) {
        var initialState = true

        class DayViewContainer(
            view: View
        ) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.

            val binding = CalendarDayLayoutBinding.bind(view)
            val calendarDayTextView: TextView = binding.calendarDayText

            init {
                view.setOnClickListener {
                    updateSelectedDate(day, calendarMap)
                    initialState = false
                }
            }
        }

        class CalendarDayBinder :
            MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data

                val context = container.binding.root.context
                container.binding.calendarDayText.text = data.date.dayOfMonth.toString()
                val layout = container.binding.calendarDayLayout

                val eventIndicator = container.binding.eventIndicator
                val spanningEventIndicator = container.binding.spanningEventIndicator
                eventIndicator.background = null
                spanningEventIndicator.background = null


                val events = calendarMap[data.date]
                if (events != null) {
                    var count = events.count()
                    events.forEach {
                        val endingDate = LocalDate.parse(it.endingDate)
                        if(ChronoUnit.DAYS.between(LocalDate.parse(it.date), endingDate) > 1  ){
                            spanningEventIndicator.setBackgroundColor(context.getColor(R.color.grey))
                            count -= 1
                        }
                    }
                    when (count) {
                        0 -> {eventIndicator.background = null}
                        1 -> {
                            eventIndicator.setBackgroundColor(context.getColor(R.color.blue_secondary))
                        }
                        in 2..6 -> {
                            eventIndicator.setBackgroundColor(context.getColor(R.color.blue_secondary_variant))
                        }
                        else -> {
                            eventIndicator.setBackgroundColor(context.getColor(R.color.blue_primary))
                        }
                    }
                }
                when (data.position) {
                    DayPosition.MonthDate -> {
                        container.calendarDayTextView.setTextColor(Color.BLUE)
                        when (data.date) {
                            selectedDate -> {
                                layout.setBackgroundColor(context.getColor(R.color.orange_blue_complementary))
                            }
                            today -> {
                                layout.setBackgroundColor(context.getColor(R.color.orange_blue_complementary_secondary))
                            }
                            else -> {
                                layout.setBackgroundColor(Color.GRAY)
                            }

                        }
                    }
                    else -> {
                        container.calendarDayTextView.setTextColor(Color.GRAY)
                    }
                }

            }
        }
        calendarView.dayBinder = CalendarDayBinder()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) // Available from the library


        val titlesContainer: ViewGroup = binding.titlesContainer.root
        titlesContainer.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                val dayOfWeek = daysOfWeek[index]
                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                textView.text = title
            }
        binding.calendarView.monthScrollListener = {
            updateTitle()
            if (initialState) {
                selectedDate?.let {
                    binding.calendarView.notifyDateChanged(it)
                    updateAdapterForDate(selectedDate, calendarMap)
                    initialState = false
                }
            } else {
                selectedDate?.let {
                    selectedDate = null
                    binding.calendarView.notifyDateChanged(it)
                    updateAdapterForDate(null, calendarMap)
                }
            }
        }

        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updateTitle() {
        val month = binding.calendarView.findFirstVisibleMonth()?.yearMonth ?: return
        binding.yearText.text = month.year.toString()
        binding.monthText.text = month.month.toString()

    }

    private fun updateAdapterForDate(
        date: LocalDate?,
        calendarMap: Map<LocalDate, MutableList<CalendarEntry>>
    ) {


        calendarDayRecyclerViewAdapter.dayEntries.clear()
        if (calendarMap[date] != null) {
            calendarMap[date]!!.sortedBy { it.startTime }
            calendarDayRecyclerViewAdapter.dayEntries.addAll(calendarMap[date]!!.sortedBy { it.startTime })
        }
        calendarDayRecyclerViewAdapter.notifyDataSetChanged()
    }

    private fun updateSelectedDate(
        day: CalendarDay,
        calendarMap: Map<LocalDate, MutableList<CalendarEntry>>
    ) {
        if (day.position == DayPosition.MonthDate) {
            if (selectedDate != day.date) {
                val oldDate = selectedDate
                selectedDate = day.date
                val binding = this@CalendarHomeFragment.binding
                binding.calendarView.notifyDateChanged(day.date)
                binding.calendarView
                oldDate?.let { binding.calendarView.notifyDateChanged(it) }
                updateAdapterForDate(day.date, calendarMap)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        calendarDayRecyclerViewAdapter = CalendarDayRecyclerViewAdapter()
        recyclerView = binding.dayEntriesRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = calendarDayRecyclerViewAdapter


    }


    private fun getData() {
        val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        val wgObserver = androidx.lifecycle.Observer<Wg> { wg ->
            wgId = wg.id.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val response =
                    calendarService.getCalendarEntries(wgId)?.mapKeys { LocalDate.parse(it.key) }
                if (response != null) {
                    calendarViewModel.setCalendarDataMap(response)
                }
                withContext(Dispatchers.Main) {
                    try {
                        Log.e("HTTP", "Great, received  $response")
                    } catch (e: HttpException) {
                        Log.e("HTTP", "Exception ${e.message}")
                    } catch (e: Throwable) {
                        Log.e("THrowable", e.toString())
                    }
                }
            }
        }
        mainViewModel.wgCurrent.observe(viewLifecycleOwner, wgObserver)
    }


    private fun addMultiDaySpanningEventsToMap() {
        getData()
        calendarViewModel.getCalendarDataMap().observe(
            viewLifecycleOwner
        )
        { map ->
            val cacheMap: MutableMap<LocalDate, MutableList<CalendarEntry>> = mutableMapOf()
            map.mapValues { entryList ->
                entryList.value.map { entry ->
                    val endingDate = LocalDate.parse(entry.endingDate)
                    if (endingDate.isAfter(entryList.key)) {
                        var diffDays: Long = ChronoUnit.DAYS.between(entryList.key, endingDate)
                        var newDate = entryList.key
                        while (diffDays != 0L) {
                            newDate = newDate.plusDays(1L)
                            if(!cacheMap.containsKey(newDate)) {
                                cacheMap[newDate] = mutableListOf(entry)
                            }
                            else{
                                cacheMap[newDate]?.add(entry)
                            }
                            diffDays -= 1
                        }

                    }
                }
            }
            val map3: MutableMap<LocalDate, MutableList<CalendarEntry>> = mutableMapOf()
            map3.putAll(map)
            cacheMap.map{
                if (map3.containsKey(it.key)){
                    map3[it.key]?.addAll(it.value)
                }
                else{
                    map3[it.key] = it.value
                }
            }
            calendarViewModel.setCalendarDataMapModified(map3)

        }
    }




}
