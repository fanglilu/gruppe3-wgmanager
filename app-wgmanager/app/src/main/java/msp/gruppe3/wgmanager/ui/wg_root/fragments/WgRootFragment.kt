package msp.gruppe3.wgmanager.ui.wg_root.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import msp.gruppe3.wgmanager.MainActivity
import msp.gruppe3.wgmanager.MainViewModel
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.common.StringBuilderUtil
import msp.gruppe3.wgmanager.databinding.FragmentWgRootBinding
import msp.gruppe3.wgmanager.models.Wg
import msp.gruppe3.wgmanager.models.dtos.RoleWgDto
import msp.gruppe3.wgmanager.ui.wg_root.recycler_view.MyWgSelectAdapter
import java.util.UUID

// Tag for logging
private const val TAG = "WG ROOT"

/**
 * This fragment shows the option between create and join a wg
 * It also makes it possible to switch between different wg
 *
 * @author Marcello Alte
 */
class WgRootFragment : Fragment() {

    private var _binding: FragmentWgRootBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    // For the recycler view which contains the expenses
    private lateinit var adapter: MyWgSelectAdapter
    private lateinit var recyclerView: RecyclerView


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWgRootBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize observer
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        initObserver()

        // Initialize buttons
        binding.buttonWgCreate.setOnClickListener { findNavController().navigate(R.id.wgCreateFragment) }
        binding.buttonWgJoin.setOnClickListener { findNavController().navigate(R.id.wgJoinFragment) }
        binding.wgDetailButtonLogoutWg.setOnClickListener { val mainActivity = requireActivity() as MainActivity
        mainActivity.logout()}

        return root
    }

    /**
     * Initialize observer to show the right content
     */
    private fun initObserver() {
        // Recycler view of expenses
        val wgListObserver = Observer<List<RoleWgDto>> {
            setLabels(it)
        }
        mainViewModel.wgList.observe(viewLifecycleOwner, wgListObserver)

        // Current wg
        val wgCurrentObserver = Observer<Wg> {
            setCurrentWg(it)
        }
        mainViewModel.wgCurrent.observe(viewLifecycleOwner, wgCurrentObserver)
    }

    private fun setLabels(wgList: List<RoleWgDto>) {
        if (wgList.isEmpty()) {
            binding.wgOverviewListCurrentWg.text = getString(R.string.welcome_msg)
            binding.wgOverviewCurrent.rlRv.visibility = View.INVISIBLE
            binding.wgOverviewListYourWg.text = ""
        } else if (wgList.size == 1) {
            binding.wgOverviewCurrent.rlRv.visibility = View.VISIBLE
            binding.wgOverviewListYourWg.text = ""
        } else {
            binding.wgOverviewCurrent.rlRv.visibility = View.VISIBLE
            setRecyclerView(wgList)
        }
    }

    private fun setCurrentWg(wg: Wg) {
        Log.e(TAG, "setCurrentWg $wg")
        binding.wgOverviewCurrent.wgListItemName.text = wg.name
        binding.wgOverviewCurrent.wgListItemDate.text = buildString {
            append("Created ")
            append(StringBuilderUtil.formatDate(wg.createdDate))
        }
        binding.wgOverviewCurrent.rlRv.setOnClickListener { goToDetailView(wg.id) }
    }

    private fun goToDetailView(id: UUID) {
        val bundle = Bundle()
        bundle.putString("id", id.toString())
        findNavController().navigate(R.id.wgDetailFragment, bundle)
    }

    private fun setRecyclerView(wgList: List<RoleWgDto>) {
        var wgListFiltered = wgList
        val currentWg = mainViewModel.wgCurrent.value
        // Remove current Wg from list, its already shown
        if (currentWg != null) {
            wgListFiltered = wgList.filter { it.id !=  currentWg.id}
        }
        val layoutManager = LinearLayoutManager(context)

        adapter = MyWgSelectAdapter(wgListFiltered)

        recyclerView = requireView().findViewById(R.id.wg_detail_recycler_view_features)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        binding.wgOverviewListCurrentWg.text = getString(R.string.current_wg)
        binding.wgOverviewListYourWg.text = getString(R.string.your_wgs)
    }
}