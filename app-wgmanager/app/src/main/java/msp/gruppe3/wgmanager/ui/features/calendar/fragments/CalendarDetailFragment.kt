package msp.gruppe3.wgmanager.ui.features.calendar.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import msp.gruppe3.wgmanager.databinding.FragmentCalendarDetailBinding
import msp.gruppe3.wgmanager.models.Wg
import msp.gruppe3.wgmanager.models.dtos.CalendarEntryCreateDto
import msp.gruppe3.wgmanager.models.dtos.ReducedUserDto
import msp.gruppe3.wgmanager.services.CalendarService
import msp.gruppe3.wgmanager.ui.features.calendar.CalendarViewModel
import msp.gruppe3.wgmanager.ui.features.calendar.adapters.CalenderAvailableUsersRecyclerViewAdapter
import msp.gruppe3.wgmanager.ui.features.calendar.adapters.CalenderSelectedUsersRecyclerViewAdapter
import msp.gruppe3.wgmanager.ui.uiElements.DateTimePickersWithStartAndEndViewInitializer
import retrofit2.HttpException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

// TODO:  Delete
class CalendarDetailFragment : Fragment() {
    private var _binding: FragmentCalendarDetailBinding? = null


    private val binding get() = _binding!!


    private lateinit var calendarService: CalendarService
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var mainViewModel: MainViewModel

    private lateinit var dtpwsaevi: DateTimePickersWithStartAndEndViewInitializer


    private lateinit var startDate: LocalDate
    private lateinit var endDate: LocalDate
    private lateinit var startTime: LocalTime
    private lateinit var endingTime: LocalTime
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var wgId: String
    private lateinit var userId: String
    private lateinit var calendarId: String
    private lateinit var token: String
    var isEditable: Boolean = false

    private lateinit var editEventTitle: EditText
    private lateinit var editEventDescription: EditText


    private lateinit var recyclerView: RecyclerView
    private lateinit var editAdapter: CalenderAvailableUsersRecyclerViewAdapter
    private lateinit var readonlyAdapter: CalenderSelectedUsersRecyclerViewAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCalendarDetailBinding.inflate(inflater, container, false)
        calendarId = arguments?.getString("calendarId").toString()
        userId = arguments?.getString("userId").toString()


        calendarViewModel = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        editEventTitle = binding.editEventTitle
        editEventDescription = binding.editEventDescription
        editEventDescription.inputType = InputType.TYPE_NULL
        editEventTitle.inputType = InputType.TYPE_NULL


        binding.editStartDate.isEnabled = false
        binding.editStartTime.isEnabled = false
        binding.editEndDate.isEnabled = false
        binding.editEndTime.isEnabled = false
        binding.editEntryButton.visibility = View.INVISIBLE
        binding.deleteEntryButton.visibility = View.INVISIBLE



        token = TokenUtil.getTokenByActivity(requireActivity())
        calendarService = CalendarService(token)
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                calendarService.getCalendarEntry(UUID.fromString(calendarId))
            if (response != null) {
                calendarViewModel.setCalendarEntry(response)
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



        binding.backToCalendarButton.setOnClickListener {
            _binding = null
            findNavController().navigate(R.id.calendarHomeFragment)
        }


        return binding.root
    }

    private fun handleUserData(data: Set<ReducedUserDto>) {
        calendarViewModel.addUsers(data)
    }

    private fun initEditAdapter() {

        fun <T, U> List<T>.intersect(intersectSet: Set<U>, filterPredicate: (T, U) -> Boolean) =
            filter { m -> intersectSet.any { filterPredicate(m, it) } }

        val wgObserver = androidx.lifecycle.Observer<Wg> { wg ->

            this.wgId = wg.id.toString()
            calendarViewModel.getCalendarEntry().observe(viewLifecycleOwner) { entry ->
                val intersectedList =
                    wg.userList.map { user -> ReducedUserDto(user.id, user.name) }
                        .intersect(
                            entry.userOnCalendarList.toSet()
                        ) { a, b ->
                            a.id == b.id
                        }
                editAdapter =
                    CalenderAvailableUsersRecyclerViewAdapter(
                        requireContext(),
                        wg.userList,
                        intersectedList
                    ) { roleUserDtos ->
                        handleUserData(
                            roleUserDtos
                        )
                    }
            }


        }
        mainViewModel.wgCurrent.observe(viewLifecycleOwner, wgObserver)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        recyclerView = binding.editCalenderUserListView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager


        calendarViewModel.getCalendarEntry().observe(viewLifecycleOwner) { calendarEntry ->
            mainViewModel.userCurrent.observe(viewLifecycleOwner) { currentUser ->

                if (currentUser.id == UUID.fromString(userId)) {

                    initEditAdapter()
                    binding.editEntryButton.visibility = View.VISIBLE
                    binding.deleteEntryButton.visibility = View.VISIBLE
                    binding.editEntryButton.setOnClickListener {
                        isEditable = isEditable == false

                        if (isEditable) {

                            binding.editStartDate.isEnabled = true
                            binding.editStartTime.isEnabled = true
                            binding.editEndDate.isEnabled = true
                            binding.editEndTime.isEnabled = true
                            recyclerView.adapter = editAdapter
                            editEventDescription.inputType = InputType.TYPE_CLASS_TEXT
                            editEventTitle.inputType = InputType.TYPE_CLASS_TEXT


                        } else {
                            binding.editStartDate.isEnabled = false
                            binding.editStartTime.isEnabled = false
                            binding.editEndDate.isEnabled = false
                            binding.editEndTime.isEnabled = false
                            editEventDescription.inputType = InputType.TYPE_NULL
                            editEventTitle.inputType = InputType.TYPE_NULL
                            recyclerView.adapter = readonlyAdapter
                            if (editEventTitle.text.toString() != "") {

                                val calendarEntryCreateDto = CalendarEntryCreateDto(
                                    creator = UUID.fromString(userId),
                                    wgId = UUID.fromString(wgId),
                                    title = editEventTitle.text.toString(),
                                    date = dtpwsaevi.startDate.toString(),
                                    startTime = dtpwsaevi.startTime.toString(),
                                    endingDate = dtpwsaevi.endDate.toString(),
                                    endingTime = dtpwsaevi.endTime.toString(),
                                    description = editEventDescription.text.toString(),
                                    userOnCalendarList = emptyList()

                                )

                                calendarViewModel.getUsers()
                                    .observe(viewLifecycleOwner) { users ->
                                        calendarEntryCreateDto.userOnCalendarList = users.toList()
                                    }
                                val calendarService = CalendarService(token)
                                CoroutineScope(Dispatchers.IO).launch {

                                    val response =
                                        calendarService.updateCalendarEntry(
                                            UUID.fromString(calendarId), calendarEntryCreateDto
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
                                    if (response != null) {
                                        calendarViewModel.setCalendarEntry(response)
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

                }
            }



            startDate = LocalDate.parse(calendarEntry.date)
            endDate = LocalDate.parse(calendarEntry.endingDate)
            startTime = LocalTime.parse(calendarEntry.startTime)
            endingTime = LocalTime.parse(calendarEntry.endingTime)
            title = calendarEntry.title
            description = calendarEntry.description

            editEventTitle.setText(title)
            editEventDescription.setText(description)
            binding.editStartDate.text = calendarEntry.date
            binding.editEndDate.text = calendarEntry.endingDate
            binding.editStartTime.text =
                startTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString()

            binding.editEndTime.text =
                endingTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString()



            readonlyAdapter =
                CalenderSelectedUsersRecyclerViewAdapter(calendarEntry.userOnCalendarList)

            recyclerView.adapter = readonlyAdapter

            dtpwsaevi =
                DateTimePickersWithStartAndEndViewInitializer(
                    requireContext(),
                    startDate,
                    endDate,
                    startTime,
                    endingTime
                )
            dtpwsaevi.initDatePicker(
                binding.editStartDate, binding.editStartTime,
                binding.editStartDate, binding.editEndDate, binding.editStartTime,
                binding.editEndTime, binding.editStartTime, binding.editEndTime
            )
            dtpwsaevi.initTimePicker(
                binding.editStartTime, binding.editEndTime,
                binding.editStartTime, binding.editEndTime
            )


        }


        binding.deleteEntryButton.setOnClickListener {


            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Are you sure you want to delete?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val response =
                            calendarService.deleteCalendarEntry(UUID.fromString(calendarId))
                        withContext(Dispatchers.Main) {
                            try {
                                Toast.makeText(context, response?.msg, Toast.LENGTH_LONG).show()
                                Log.e("HTTP", "Great, received  $response")
                            } catch (e: HttpException) {
                                Log.e("HTTP", "Exception ${e.message}")
                            } catch (e: Throwable) {
                                Log.e("THrowable", e.toString())
                            }
                        }
                    }
                    findNavController().navigate(R.id.calendarHomeFragment)
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()


        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}