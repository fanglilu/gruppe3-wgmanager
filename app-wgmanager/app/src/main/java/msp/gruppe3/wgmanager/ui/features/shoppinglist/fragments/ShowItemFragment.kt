package msp.gruppe3.wgmanager.ui.features.shoppinglist.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.databinding.FragmentShowItemsBinding
import msp.gruppe3.wgmanager.models.Item
import msp.gruppe3.wgmanager.models.ShoppingListEntry
import msp.gruppe3.wgmanager.services.ItemsService
import msp.gruppe3.wgmanager.ui.features.shoppinglist.ShoppingListViewModel
import msp.gruppe3.wgmanager.ui.features.shoppinglist.recycler_view.MyItemSelectAdapter
import retrofit2.HttpException
import java.util.*

// Fragment associated with the showing of all items on the list passed; Called from ShowShoppingListFragment

class ShowItemFragment : Fragment() {

    private var _binding: FragmentShowItemsBinding? = null

    private lateinit var adapter: MyItemSelectAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var listViewModel: ShoppingListViewModel
    private lateinit var currentList: String
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowItemsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        listViewModel = ViewModelProvider(requireActivity())[ShoppingListViewModel::class.java]

        currentList = arguments?.getString("listSelected").toString()

        // Send the request to get all the items on the list provided to the server
        getItems(currentList)
        // Init all the buttons used in the UI
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
        val itemObserver = Observer<List<Item>> { items ->
            adapter = context?.let { MyItemSelectAdapter(items, it, findNavController(), requireActivity()) }!!

            recyclerView = requireView().findViewById(R.id.recyclerShowItemsOnList)
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
        listViewModel.items.observe(viewLifecycleOwner, itemObserver)
    }

    // This is used to request the current items from the server when e.g. an item is added/deleted/modified
    override fun onResume() {
        super.onResume()
        getItems(currentList)
    }

    private fun initButton() {
        // Sets the downswipe for updates from the server up
        val swipeContainer =binding.shoppingListShowItemsSwipeContainer
        swipeContainer.setOnRefreshListener {
            getItems(currentList)
            swipeContainer.isRefreshing = false
        }

        // Sets up the button to create a new item
        val newItemButton = binding.shoppingListCreateNewItemButton
        newItemButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                // adds the fragment required to create a new item to the backstack
                val bundle = Bundle()
                bundle.putString("listSelected", currentList)
                findNavController().navigate(R.id.createNewItemFragment, bundle)
            }
        })
    }

    // Updated the items currently displayed with the ones provided
    private fun updateItems(items: List<Item>) {
        listViewModel.items.value = items
    }

    // Inits the request for items to the server
    private fun getItems(listId: String) {
        val token = TokenUtil.getTokenByActivity(requireActivity())
        val itemService = ItemsService(token)
        val listIdUUID = UUID.fromString(listId)

        // Starts the request from the serveras
        CoroutineScope(Dispatchers.IO).launch {
            val response = itemService.getItems(listIdUUID)
            withContext(Dispatchers.Main) {
                try {
                    if (response != null) {
                        updateItems(response)
                    }
                } catch (e: HttpException) {
                    Log.e("HTTP", "Exception: Show items ${e.message}")
                } catch (e: Throwable) {
                    Log.e("Throwable", "Exception: Show items ${e.toString()}")
                }
            }
        }
    }
}