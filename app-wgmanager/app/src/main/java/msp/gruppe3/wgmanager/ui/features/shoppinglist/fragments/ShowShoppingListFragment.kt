package msp.gruppe3.wgmanager.ui.features.shoppinglist.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
import msp.gruppe3.wgmanager.databinding.FragmentShowShoppingListBinding
import msp.gruppe3.wgmanager.models.ShoppingListEntry
import msp.gruppe3.wgmanager.models.Wg
import msp.gruppe3.wgmanager.models.dtos.GetListDto
import msp.gruppe3.wgmanager.models.dtos.UserDto
import msp.gruppe3.wgmanager.services.ListService
import msp.gruppe3.wgmanager.ui.features.shoppinglist.ShoppingListViewModel
import msp.gruppe3.wgmanager.ui.features.shoppinglist.recycler_view.MyListSelectAdapter
import retrofit2.HttpException
import java.util.*

// Fragment associated with the showing of all shopping lists from the current WG

class ShowShoppingListFragment : Fragment() {

    private var _binding: FragmentShowShoppingListBinding? = null

    private lateinit var adapter: MyListSelectAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var mainViewModel: MainViewModel
    private lateinit var listViewModel: ShoppingListViewModel
    private lateinit var wgID: String
    private var toolbar: Toolbar? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowShoppingListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Request the lists for the current WG from the server
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        listViewModel = ViewModelProvider(requireActivity())[ShoppingListViewModel::class.java]
        val wgObserver = Observer<Wg> { wg ->
            wgID = wg.id.toString()
            getLists(UUID.fromString(wgID))
        }
        mainViewModel.wgCurrent.observe(viewLifecycleOwner, wgObserver)

        // initialize UI components
        initButton()

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    return findNavController().navigate(R.id.homeFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the recycler view
        val layoutManager = LinearLayoutManager(context)
        val listObserver = Observer<List<ShoppingListEntry>> { lists ->
            adapter = context?.let { activity?.let { it1 -> MyListSelectAdapter(it1, lists, it, findNavController(), requireActivity()) } }!!

            recyclerView = binding.recyclerShowShoppingLists
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
        listViewModel.shoppingLists.observe(viewLifecycleOwner, listObserver)
    }

    // This is used to request the current lists from the server when e.g. a list is added or deleted
    override fun onResume() {
        super.onResume()
        getLists(UUID.fromString(wgID))
    }

    private fun initButton() {

        // Setup the downswipe to update the lists
        val swipeContainer =binding.shoppingListShowViewSwipeContainer
        // When the swipe is done, the current lists are requested from the server
        swipeContainer.setOnRefreshListener {
            getLists(UUID.fromString(wgID))
            swipeContainer.isRefreshing = false
        }

        // Init the button to create a new list
        val newListButton = binding.shoppingListCreateNewListButton
        // Changes the current fragment with the one required to make a new list
        newListButton.setOnClickListener {
            findNavController().navigate(R.id.createNewListFragment)
        }

        // Init the shopping mode button
        val shoppingModeButton = binding.shoppingListShoppingModeButton
        // Changes the current fragment with the one required for the shopping mode
        shoppingModeButton.setOnClickListener {
            findNavController().navigate(R.id.showListsForShoppingListFragment)
        }
    }

    // Takes care of changing the lists currently displayed with the ones supplied
    private fun updateLists(lists: List<ShoppingListEntry>) {
        listViewModel.shoppingLists.value = lists
    }

    // Inits the request for the current lists
    private fun getLists(wgId: UUID) {
        val token = TokenUtil.getTokenByActivity(requireActivity())
        val listService = ListService(token)
        val userObserver = Observer<UserDto> { user ->
            val lists = GetListDto(
                wgID = wgId,
                requester = user.id
            )
            // Starts the request from the server
            CoroutineScope(Dispatchers.IO).launch {
                val response = listService.findMyLists(lists)
                withContext(Dispatchers.Main) {
                    try {
                        if (response != null) {
                            // When we get the current lists from the server, we pass them to be updated in the UI
                            updateLists(response)
                        }
                    } catch (e: HttpException) {
                        Log.e("HTTP", "Get lists: ${e.message}")
                    } catch (e: Throwable) {
                        Log.e("Throwable exception", "Get lists: ${e.toString()}")
                    }
                }
            }
        }
        mainViewModel.userCurrent.observe(viewLifecycleOwner, userObserver)
    }
}