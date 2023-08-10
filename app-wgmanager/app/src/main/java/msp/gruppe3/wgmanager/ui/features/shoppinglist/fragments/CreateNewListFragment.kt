package msp.gruppe3.wgmanager.ui.features.shoppinglist.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import msp.gruppe3.wgmanager.MainViewModel
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.databinding.FragmentCreateListBinding
import msp.gruppe3.wgmanager.models.Wg
import msp.gruppe3.wgmanager.models.dtos.RegisterListDto
import msp.gruppe3.wgmanager.models.dtos.UserDto
import msp.gruppe3.wgmanager.services.ListService
import msp.gruppe3.wgmanager.ui.features.shoppinglist.ShoppingListViewModel
import retrofit2.HttpException
import java.util.*

// Fragment to create a new list; Called from ShowShoppingListFragment

class CreateNewListFragment : Fragment() {
    private var _binding: FragmentCreateListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Init the buttons used
        initButton()
    }

    // Creates the onClickListener for the button to send the new list to the server
    private fun initButton() {
        val newListButton = binding.createNewListButton
        newListButton.setOnClickListener { createNewList(binding.enterNameNewList.text.toString()) }
    }

    // Creates the DTO for a new list which is then send to the server
    private fun createNewList(name: String) {
        val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        val token = TokenUtil.getTokenByActivity(requireActivity())

        val listService = ListService(token)
        // Get the current WG id and user id so the item can be associated with the correct WG and user
        val wgObserver = Observer<Wg> { wg ->
            val userObserver = Observer<UserDto> { user ->

                val newList = RegisterListDto(
                    name = name,
                    wgID = wg.id,
                    creator = user.id,
                    value = 0.0,
                    numItems = 0,
                    private = binding.privateSwitch.isChecked
                )

                // Starts the request from the server
                CoroutineScope(Dispatchers.IO).launch {
                    val response = listService.createList(newList)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response != null) {
                                // When we get the current lists from the server, we pass them to be updated in the UI
                                val listViewModel = ViewModelProvider(requireActivity())[ShoppingListViewModel::class.java]
                                listViewModel.shoppingLists.value = response
                                findNavController().navigate(R.id.showShoppingListFragment)                            }
                            Log.e("HTTP", "Received new list  $response")
                        } catch (e: HttpException) {
                            Log.e("HTTP", "Exception: New list ${e.message}")
                        } catch (e: Throwable) {
                            Log.e("Throwable", "Exception: New list ${e.toString()}")
                        }
                    }
                }
            }
            mainViewModel.userCurrent.observe(viewLifecycleOwner, userObserver)
        }
        mainViewModel.wgCurrent.observe(viewLifecycleOwner, wgObserver)
    }
}