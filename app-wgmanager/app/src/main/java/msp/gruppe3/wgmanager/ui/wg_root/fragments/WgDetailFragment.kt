package msp.gruppe3.wgmanager.ui.wg_root.fragments

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import msp.gruppe3.wgmanager.common.StringBuilderUtil
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.databinding.FragmentWgDetailBinding
import msp.gruppe3.wgmanager.enums.Features
import msp.gruppe3.wgmanager.models.Wg
import msp.gruppe3.wgmanager.models.dtos.SuccessDto
import msp.gruppe3.wgmanager.services.AlarmReceiver
import msp.gruppe3.wgmanager.services.WgService
import msp.gruppe3.wgmanager.ui.wg_root.recycler_view.MyFeatureSelectAdapter
import msp.gruppe3.wgmanager.ui.wg_root.recycler_view.MyFeatureSelectAdapterWithoutCheckbox
import retrofit2.HttpException
import java.util.*


// Tag for logging
private const val TAG = "WG DETAIL"

/**
 * This class displays the wg details. It uses a recycler view to show the feature options as
 * checkboxes to the user.
 *
 * @author Marcello Alte
 */
class WgDetailFragment : Fragment() {

    private var _binding: FragmentWgDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var wg: Wg

    // For the recycler view which contains the feature options
    private lateinit var adapter: MyFeatureSelectAdapterWithoutCheckbox
    private lateinit var recyclerView: RecyclerView

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        _binding = FragmentWgDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // UI
        handleArguments()
        binding.wgDetailButtonSubmit.setOnClickListener { handleButtonSwitchWg() }
        binding.wgDetailButtonLeaveWg.setOnClickListener { val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to leave this wg?")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, id ->
                    handleButtonLeaveWg()
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show() }
        binding.wgDetailButtonDeleteWg.setOnClickListener { val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to delete this wg for all members?")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, id ->
                    handleButtonLeaveWg()
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show() }
        binding.wgDetailButtonInvitationCode.setOnClickListener { handleButtonGetInvitationCode() }

        return root
    }

    private fun handleArguments() {
        // Update Expense if id argument is passed
        val id = arguments?.getString("id")
        if ((id != null) && id.isNotEmpty()) {
            Log.d(TAG, "Got id from nav argument $id")
            getWg(id)
        } else {
            Log.e(TAG, "No arguments passed $id")
            this.findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun handleButtonSwitchWg() {
        mainViewModel.wgCurrent.value = wg
        this.findNavController().navigate(R.id.homeFragment)
    }

    private fun buildRecyclerView(featureList: List<Features>) {
        // Initialize the recycler view
        val layoutManager = LinearLayoutManager(context)
        adapter = MyFeatureSelectAdapterWithoutCheckbox(featureList.toTypedArray())

        recyclerView = requireView().findViewById(R.id.wg_detail_recycler_view_features)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun setWgDetails(wg: Wg) {
        Log.d(TAG, "setWgDetails $wg")
        binding.wgDetailName.text = wg.name

        binding.wgDetailCreationDate.text = buildString {
            append("Created ")
            append(StringBuilderUtil.formatDate(wg.createdDate))
        }
        buildRecyclerView(wg.features.map { it.name })
    }

    private fun getWg(id: String) {
        val token = TokenUtil.getTokenByActivity(requireActivity())
        val wgService = WgService(token)

        CoroutineScope(Dispatchers.IO).launch {

            val response = wgService.getById(UUID.fromString(id))
            withContext(Dispatchers.Main) {
                try {
                    Log.d(TAG, "getWg response: $response")
                    if (response != null) {
                        wg = response
                        setWgDetails(wg)
                    } else {
                        showToast("Sorry something went wrong.")
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "Exception ${e.message}")
                } catch (e: Throwable) {
                    Log.e(TAG, e.toString())
                }
            }
        }
    }

    private fun handleButtonGetInvitationCode() {
        val token = TokenUtil.getTokenByActivity(requireActivity())
        val wgService = WgService(token)

        CoroutineScope(Dispatchers.IO).launch {

            val response = wgService.getInvitationCodeWg(wg.id)
            withContext(Dispatchers.Main) {
                try {
                    Log.d(TAG, "getInvitationCode response: $response")
                    if (response != null) {
                        binding.wgDetailCode.text = response
                        showToast("Code updated")
                    } else {
                        showToast("Sorry something went wrong.")
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "Exception ${e.message}")
                } catch (e: Throwable) {
                    Log.e(TAG, e.toString())
                }
            }
        }
    }

    private fun handleWgLeaveOrDeleteResponse(response: SuccessDto?, token: String) {
        if (response != null) {
            if (response.success) {
                mainViewModel.wgCurrent.value = null
                mainViewModel.updateWgList(requireActivity())
                findNavController().navigate(R.id.wgRootFragment)
            }
        } else {
            showToast("Sorry something went wrong.")
        }
    }

    private fun handleButtonDeleteWg() {
        val token = TokenUtil.getTokenByActivity(requireActivity())
        val wgService = WgService(token)

        CoroutineScope(Dispatchers.IO).launch {

            val response = wgService.deleteWg(wg.id)
            withContext(Dispatchers.Main) {
                try {
                    Log.d(TAG, "deleteWg response: $response")
                    handleWgLeaveOrDeleteResponse(response, token)
                } catch (e: HttpException) {
                    Log.e(TAG, "Exception ${e.message}")
                } catch (e: Throwable) {
                    Log.e(TAG, e.toString())
                }
            }
        }
    }

    private fun handleButtonLeaveWg() {
        val token = TokenUtil.getTokenByActivity(requireActivity())
        val wgService = WgService(token)

        CoroutineScope(Dispatchers.IO).launch {

            val response = wgService.leaveWg(wg.id)
            withContext(Dispatchers.Main) {
                try {
                    Log.d(TAG, "leaveWg response: $response")
                    handleWgLeaveOrDeleteResponse(response, token)
                } catch (e: HttpException) {
                    Log.e(TAG, "Exception ${e.message}")
                } catch (e: Throwable) {
                    Log.e(TAG, e.toString())
                }
            }
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