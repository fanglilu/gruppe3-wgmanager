package msp.gruppe3.wgmanager.ui.features.shoppinglist.fragments

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.databinding.FragmentCreateItemBinding
import msp.gruppe3.wgmanager.models.dtos.RegisterItemDto
import msp.gruppe3.wgmanager.models.dtos.UserDto
import msp.gruppe3.wgmanager.services.ItemsService
import retrofit2.HttpException
import msp.gruppe3.wgmanager.MainViewModel
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.models.ShoppingListEntry
import msp.gruppe3.wgmanager.ui.features.shoppinglist.ShoppingListViewModel

// Fragment associated with the showing of the fields to create a new item; Called from ShowItemFragment

class CreateNewItemFragment: Fragment() {
    private var _binding: FragmentCreateItemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var currentList: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateItemBinding.inflate(inflater, container, false)
        val root: View = binding.root
        currentList = arguments?.getString("listSelected").toString()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inits all buttons used
        initButton()
    }

    // Sets up the button to send the new item to the server to be saved
    private fun initButton() {
        val newItemButton = binding.createNewItemButton
        newItemButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                // Creates the item with the name and description typed in by the user
                createNewItem(binding.enterNameNewItem.text.toString(), binding.enterDescriptionNewItem.text.toString())
            }
        })
    }

    // Creates the DTO to send the new item to the server and then inits this transfer
    private fun createNewItem(name: String, description: String) {
        val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        val token = TokenUtil.getTokenByActivity(requireActivity())
        val itemService = ItemsService(token)
        // Create the DTO with the users id
        val userObserver = Observer<UserDto> { user ->
            val newItem = RegisterItemDto(
                name = name,
                description = description,
                listID = currentList,
                isBought = false,
                owner = user.id
            )

            // Starts the request from the server
            CoroutineScope(Dispatchers.IO).launch {
                val response = itemService.createItem(newItem)
                withContext(Dispatchers.Main) {
                    try {
                        if (response != null) {
                            // When we get the current lists from the server, we pass them to be updated in the UI
                            val listViewModel = ViewModelProvider(requireActivity())[ShoppingListViewModel::class.java]
                            listViewModel.items.value = response
                            val bundle = Bundle()
                            bundle.putString("listSelected", currentList)
                            findNavController().navigate(R.id.showItemFragment, bundle)
                        }
                    } catch (e: HttpException) {
                        Log.e("HTTP", "Exception: Create item: ${e.message}")
                    } catch (e: Throwable) {
                        Log.e("Throwable", "Exception: Create item: ${e.toString()}")
                    }
                }
            }
        }
        mainViewModel.userCurrent.observe(viewLifecycleOwner, userObserver)
    }
}