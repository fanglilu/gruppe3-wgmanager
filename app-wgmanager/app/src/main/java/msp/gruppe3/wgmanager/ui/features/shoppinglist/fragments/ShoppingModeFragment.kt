package msp.gruppe3.wgmanager.ui.features.shoppinglist.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import msp.gruppe3.wgmanager.databinding.FragmentShoppingModeBinding
import msp.gruppe3.wgmanager.enums.Features
import msp.gruppe3.wgmanager.models.Feature
import msp.gruppe3.wgmanager.models.Item
import msp.gruppe3.wgmanager.services.ItemsService
import msp.gruppe3.wgmanager.ui.features.shoppinglist.recycler_view.MyShoppingModeSelectAdapter
import retrofit2.HttpException
import java.util.*

// Fragment associated with showing the shopping mode; Called from ShowShoppingListFragment
class ShoppingModeFragment : Fragment() {

    private var _binding: FragmentShoppingModeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MyShoppingModeSelectAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var currentListID: List<String>
    private var itemsLoaded: List<Item> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingModeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        currentListID = arguments?.getStringArrayList("listsSelected")!!.toList()

        // Gets all the items from the lists provided from the server
        getItems(currentListID)
        // Init all the buttons used
        initButton()

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    return findNavController().navigate(R.id.showShoppingListFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the recycler view
        val layoutManager = LinearLayoutManager(context)
        adapter = context?.let { MyShoppingModeSelectAdapter(this.itemsLoaded, it) }!!

        recyclerView = requireView().findViewById(R.id.recycler_Show_Items_On_List_shopping_mode)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    // Updated the recycler with the current items
    private fun updateRecycler() {
        val layoutManager = LinearLayoutManager(context)
        adapter = context?.let { MyShoppingModeSelectAdapter(this.itemsLoaded, it) }!!

        recyclerView = requireView().findViewById(R.id.recycler_Show_Items_On_List_shopping_mode)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun initButton() {

        // Sets up the downswipe to get the current items from the server
        val swipeContainer =binding.shoppingModeShowItemsSwipeContainer
        swipeContainer.setOnRefreshListener {
            getItems(currentListID)
            swipeContainer.isRefreshing = false
        }

        // Inits the finish shopping mode button to go to the add expense screen
        val finishShoppingButton = binding.shoppingModeFinishShoppingButton
        finishShoppingButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
                for (feature in mainViewModel.wgCurrent.value?.features!!) {
                    if (feature.name == Features.FINANCE) {
                        val bundle = Bundle()
                        bundle.putString("description", "Shopped ${itemsLoaded.size} items")
                        findNavController().navigate(R.id.financeAddExpenseFragment, bundle)
                    }
                }
            }
        })
    }

    // Updates the items currently displayed with the ones provided
    private fun updateItems(items: List<Item>) {
        this.itemsLoaded = items
        this.updateRecycler()
    }

    // Inits the request for the items on all the lists provided from the server
    private fun getItems(listId: List<String>) {
        val token = TokenUtil.getTokenByActivity(requireActivity())
        val itemService = ItemsService(token)
        val listIdUUID = listId.map { UUID.fromString(it) }

        // Starts the request from the server
        CoroutineScope(Dispatchers.IO).launch {
            val response = itemService.getMultipleItems(listIdUUID)
            withContext(Dispatchers.Main) {
                try {
                    if (response != null) {
                        updateItems(response)
                    }
                } catch (e: HttpException) {
                    Log.e("HTTP", "Exception: Shopping mode: ${e.message}")
                } catch (e: Throwable) {
                    Log.e("Throwable", "Exception: Shopping mode: ${e.toString()}")
                }
            }
        }
    }
}