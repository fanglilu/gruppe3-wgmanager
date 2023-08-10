package msp.gruppe3.wgmanager.ui.features.shoppinglist.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.databinding.FragmentDetailsItemBinding
import msp.gruppe3.wgmanager.models.Item
import msp.gruppe3.wgmanager.models.dtos.UpdateItemDto
import msp.gruppe3.wgmanager.services.ItemsService
import msp.gruppe3.wgmanager.ui.features.shoppinglist.ShoppingListViewModel
import retrofit2.HttpException
import java.util.*

// Fragment associated with the showing the details of an item on a list; Called from ShowItemFragment

class ShowItemDetailsFragment : Fragment() {

    private var _binding: FragmentDetailsItemBinding? = null
    private lateinit var currentItemId: String
    private lateinit var currentItemName: String
    private lateinit var currentItemDescription: String
    private lateinit var currentItemIsBought: String
    private lateinit var currentItemOwner: String

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsItemBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // Inits all buttons and the correct text to be showed for the individual entries

        currentItemId = arguments?.getString("currentItemId").toString()
        currentItemName = arguments?.getString("currentItemName").toString()
        currentItemDescription = arguments?.getString("currentItemDescription").toString()
        currentItemIsBought = arguments?.getString("currentItemDescription").toString()
        currentItemOwner = arguments?.getString("currentItemOwner").toString()


        initButton()
        initText()
        return root
    }

    // Inits the button which creates the DTO of the changes done to the item and sends it to the server
    private fun initButton() {

        val updateItemButton = binding.showItemUpdateButton
        updateItemButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                // Create the DTO
                val itemToUpdate = UpdateItemDto(
                    id = UUID.fromString(currentItemId),
                    name = binding.showItemNameField.text.toString(),
                    description = binding.showItemDescriptionField.text.toString(),
                    bought = currentItemIsBought,
                )
                currentItemName = binding.showItemNameField.text.toString()
                currentItemDescription = binding.showItemDescriptionField.text.toString()
                // Send the DTO to the server
                updateItem(itemToUpdate)
            }
        })
    }

    // Pops the current fragment from the backstack and updates the lists in the ShowItemFragment
    private fun removeFragment() {
        // Get the ShowItemFragment and call the onResume() method which updates the lists showed in the UI
        parentFragmentManager.popBackStack()
    }

    // Sets the text of the corresponding fields in the UI for this item
    private fun initText(){
        binding.showItemNameField.setText(this.currentItemName)
        binding.showItemDescriptionField.setText(this.currentItemDescription)
        binding.showItemCreator.text = this.currentItemOwner
    }

    // Sends the DTO containing the changes to the server to be saved
    private fun updateItem(itemToUpdate: UpdateItemDto) {
        val token = TokenUtil.getTokenByActivity(requireActivity())
        val itemService = ItemsService(token)

        // Starts the request from the server
        CoroutineScope(Dispatchers.IO).launch {
            val response = itemService.updateItem(itemToUpdate)
            withContext(Dispatchers.Main) {
                try {
                    if (response != null) {
                        val listViewModel = ViewModelProvider(requireActivity())[ShoppingListViewModel::class.java]
                        listViewModel.items.value = response
                        removeFragment()
                    }
                } catch (e: HttpException) {
                    Log.e("HTTP", "Exception: Change details ${e.message}")
                } catch (e: Throwable) {
                    Log.e("Throwable", "Exception: Change details ${e.toString()}")
                }
            }
        }
    }
}