package msp.gruppe3.wgmanager.ui.features.shoppinglist.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.databinding.FragmentSelectListsForShoppingModeBinding
import msp.gruppe3.wgmanager.models.ShoppingListEntry
import msp.gruppe3.wgmanager.ui.features.shoppinglist.ShoppingListViewModel
import msp.gruppe3.wgmanager.ui.features.shoppinglist.recycler_view.MyShoppingListSelectionAdapter
import java.util.*

// Fragment associated with the showing of all shopping lists from the current WG

class ShowListsForShoppingListFragment() : Fragment() {

    private var _binding: FragmentSelectListsForShoppingModeBinding? = null

    private lateinit var adapter: MyShoppingListSelectionAdapter
    private lateinit var recyclerView: RecyclerView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectListsForShoppingModeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // initialize UI components
        initButton()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the recycler view
        val layoutManager = LinearLayoutManager(context)
        val listViewModel = ViewModelProvider(requireActivity())[ShoppingListViewModel::class.java]

            val listObserver = Observer<List<ShoppingListEntry>> { lists ->
            adapter = context?.let { activity?.let { it1 -> MyShoppingListSelectionAdapter(it1, lists) } }!!

            recyclerView = binding.recyclerShowListsForShoppingMode
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
        listViewModel.shoppingLists.observe(viewLifecycleOwner, listObserver)

    }

    private fun initButton() {

        // Init the shopping mode button
        val enterShoppingModeButton = binding.goToShoppingModeButton
        // Changes the current fragment with the one required for the shopping mode
        enterShoppingModeButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val bundle = Bundle()
                bundle.putStringArrayList("listsSelected", ArrayList(adapter.getCheckedFeatures()))
                findNavController().navigate(R.id.shoppingModeFragment, bundle)
            }
        })
    }
}