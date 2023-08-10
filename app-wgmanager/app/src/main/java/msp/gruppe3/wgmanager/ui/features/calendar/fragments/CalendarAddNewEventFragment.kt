package msp.gruppe3.wgmanager.ui.features.calendar.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import msp.gruppe3.wgmanager.MainViewModel
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.databinding.FragmentCalendarAddNewEventBinding
import msp.gruppe3.wgmanager.models.Wg
import msp.gruppe3.wgmanager.models.dtos.CalendarEntryCreateDto
import msp.gruppe3.wgmanager.models.dtos.ReducedUserDto
import msp.gruppe3.wgmanager.models.dtos.UserDto
import msp.gruppe3.wgmanager.services.CalendarService
import msp.gruppe3.wgmanager.ui.features.calendar.CalendarViewModel
import msp.gruppe3.wgmanager.ui.features.calendar.adapters.CalenderAvailableUsersRecyclerViewAdapter
import msp.gruppe3.wgmanager.ui.uiElements.DateTimePickersWithStartAndEndViewInitializer
import retrofit2.HttpException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


class CalendarAddNewEventFragment : Fragment() {

    private var _binding: FragmentCalendarAddNewEventBinding? = null

    private val binding get() = _binding!!


    private lateinit var calendarViewModel: CalendarViewModel

    private lateinit var mainViewModel: MainViewModel

    private lateinit var startDate: LocalDate
    private lateinit var startTime: LocalTime
    private lateinit var endDate: LocalDate
    private lateinit var endTime: LocalTime
    private lateinit var description: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalenderAvailableUsersRecyclerViewAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        calendarViewModel = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]

        _binding = FragmentCalendarAddNewEventBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val view = binding.root
        val startDateArgument = arguments?.getString("StartDate")


        // Calendar entry values initialization
        startTime =
            LocalDateTime.now()
                .toLocalTime()
        startDate = LocalDate.parse(startDateArgument)

        endDate = LocalDate.parse(startDateArgument)
        endTime = LocalDateTime.now()
            .toLocalTime()
        description = binding.addNewEventDescription.text.toString()

        binding.startDate.text = startDateArgument
        binding.startTime.hint = startTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString()
        binding.endDate.hint = startDateArgument
        binding.endTime.hint = startTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString()
        binding.addNewEventTitle.setText("")
        binding.addNewEventDescription.setText("")


        val dtpwsaevi =
            DateTimePickersWithStartAndEndViewInitializer(
                requireContext(),
                startDate,
                endDate,
                startTime,
                endTime
            )
        dtpwsaevi.initDatePicker(
            binding.startDate,
            binding.endDate,
            binding.startDate,
            binding.endDate,
            binding.startTime,
            binding.endTime,
            binding.startTime,
            binding.endTime
        )
        dtpwsaevi.initTimePicker(
            binding.startTime,
            binding.endTime,
            binding.startTime,
            binding.endTime
        )

        val token = TokenUtil.getTokenByActivity(requireActivity())


        val createNewEvent = binding.createNewEventButton.setOnClickListener {

            zipLiveDataWithNull(mainViewModel.wgCurrent, mainViewModel.userCurrent).observe(
                viewLifecycleOwner
            ) {
                val wg = it[0] as Wg
                val user = it[1] as UserDto
                if (binding.addNewEventTitle.text.toString() != "") {

                    val calendarEntryCreateDto = CalendarEntryCreateDto(
                        creator = user.id,
                        wgId = wg.id,
                        title = binding.addNewEventTitle.text.toString(),
                        date = dtpwsaevi.startDate.toString(),
                        startTime = dtpwsaevi.startTime.toString(),
                        endingDate = dtpwsaevi.endDate.toString(),
                        endingTime = dtpwsaevi.endTime.toString(),
                        description = binding.addNewEventDescription.text.toString(),
                        userOnCalendarList = emptyList()

                    )

                    calendarViewModel.getUsers()
                        .observe(viewLifecycleOwner) { users ->
                            calendarEntryCreateDto.userOnCalendarList = users.toList()
                        }
                    val calendarService = CalendarService(token)
                    CoroutineScope(Dispatchers.IO).launch {

                        val response =
                            calendarService.createCalendarEntry(
                                calendarEntryCreateDto
                            )
                        withContext(Dispatchers.Main) {
                            try {
                                Log.e("HTTP", "Great, received  $response")
                            } catch (e: HttpException) {
                                Log.e("HTTP", "Exception ${e.message}")
                            } catch (e: Throwable) {
                                Log.e("THrowable", e.toString())
                            }
                        }
                        val imm =
                            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)

                        activity?.runOnUiThread {
                            onDestroy()
                            findNavController().navigate(R.id.calendarHomeFragment)

                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Please enter a title",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        }

        val abortCreateNewEvent = binding.abort.setOnClickListener {
            findNavController().navigate(R.id.calendarHomeFragment)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        val wgObserver = androidx.lifecycle.Observer<Wg> { wg ->
            adapter =
                CalenderAvailableUsersRecyclerViewAdapter(
                    requireContext(),
                    wg.userList,
                    mutableListOf(),
                ) { reducedUserDto ->
                    handleUserData(
                        reducedUserDto
                    )
                }
            recyclerView = binding.calenderUserListView
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
        mainViewModel.wgCurrent.observe(viewLifecycleOwner, wgObserver)


    }

    private fun handleUserData(data: Set<ReducedUserDto>) {
        calendarViewModel.addUsers(data)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //Incase your LiveDatas might have null values
    fun zipLiveDataWithNull(vararg liveItems: LiveData<*>): LiveData<ArrayList<Any?>> {
        return MediatorLiveData<ArrayList<Any?>>().apply {
            val zippedObjects = arrayOfNulls<Any>(liveItems.size)
            val zippedObjectsFlag = BooleanArray(liveItems.size)
            liveItems.forEachIndexed { index, liveData ->
                addSource(liveData) { item ->
                    zippedObjects[index] = item
                    zippedObjectsFlag[index] = true
                    if (!zippedObjectsFlag.contains(false)) {
                        value = zippedObjects.toCollection(ArrayList())
                        for (i in 0 until liveItems.size) {
                            zippedObjectsFlag[i] = false
                        }
                    }
                }
            }
        }
    }


}