package msp.gruppe3.wgmanager.ui.wg_root.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import msp.gruppe3.wgmanager.databinding.FragmentWgCreateBinding
import msp.gruppe3.wgmanager.enums.Features
import msp.gruppe3.wgmanager.models.Wg
import msp.gruppe3.wgmanager.models.dtos.WgCreateDto
import msp.gruppe3.wgmanager.services.WgService
import msp.gruppe3.wgmanager.ui.wg_root.recycler_view.MyFeatureSelectAdapter
import retrofit2.HttpException


// Tag for logging
private const val TAG = "WG CREATE"

/**
 * This class handles the wg creation. It uses a recycler view to show the feature options as
 * checkboxes to the user.
 *
 * @author Marcello Alte
 */
class WgCreateFragment : Fragment() {

    private var _binding: FragmentWgCreateBinding? = null
    private val binding get() = _binding!!

    // For the recycler view which contains the feature options
    private lateinit var adapter: MyFeatureSelectAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWgCreateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // UI
        binding.wgCreateButtonSubmit.setOnClickListener { handleButtonSubmit() }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        // Initialize the recycler view
        val layoutManager = LinearLayoutManager(context)

        adapter = MyFeatureSelectAdapter(Features.values())

        recyclerView = requireView().findViewById(R.id.wg_create_recycler_view_features)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    /**
     * Submit button
     */
    private fun handleButtonSubmit() {
        val wgCreateNameText: String = binding.wgCreateNameInput.text.toString().trim()

        if (wgCreateNameText == "") {
            showToast("Please enter a wg name!")
            return
        }
        if (adapter.getCheckedFeatures().isEmpty()) {
            showToast("Please select at least one feature!")
            return
        }

        // Disable button to prevent multiple requests
        binding.wgCreateButtonSubmit.isEnabled = false

        val wgCreateDto = WgCreateDto(
            wgCreateNameText,
            adapter.getCheckedFeatures()
        )

        createWgRequest(wgCreateDto)
    }

    /**
     * Create wg request
     */
    private fun createWgRequest(wgCreateDto: WgCreateDto) {
        val token = TokenUtil.getTokenByActivity(requireActivity())
        val wgService = WgService(token)

        CoroutineScope(Dispatchers.IO).launch {
            val response = wgService.createWg(wgCreateDto)
            withContext(Dispatchers.Main) {
                try {
                    Log.e(TAG, "Response: $response")
                    if (response != null) {
                        handleCreateWgResponse(response)
                    } else {
                        showToast("Sorry something went wrong.")
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "Exception ${e.message}")
                } catch (e: Throwable) {
                    Log.e(TAG, e.toString())
                }
                binding.wgCreateButtonSubmit.isEnabled = true
            }
        }
    }

    private fun handleCreateWgResponse(response: Wg) {
        val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mainViewModel.wgCurrent.postValue(response)
        mainViewModel.updateWgList(requireActivity())
        activity?.runOnUiThread {
            val bundle = Bundle()
            bundle.putString("id", response.id.toString())
            findNavController().navigate(R.id.wgDetailFragment, bundle)
        }
    }

    /**
     * Show toast helper method
     * Length toast shown: LENGTH_SHORT
     */
    private fun showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, length).show()
    }
}