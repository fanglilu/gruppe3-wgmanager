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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import msp.gruppe3.wgmanager.MainViewModel
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.databinding.FragmentWgJoinBinding
import msp.gruppe3.wgmanager.models.Wg
import msp.gruppe3.wgmanager.services.WgService
import retrofit2.HttpException


// Tag for logging
private const val TAG = "WG JOIN"

// Also defined in Backend (WgRootService)
const val INVITATION_CODE_LENGTH = 6

/**
 * This class helps the user to join a wg
 *
 * @author Marcello Alte
 */
class WgJoinFragment : Fragment() {

    private var _binding: FragmentWgJoinBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWgJoinBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // UI
        binding.wgJoinButtonSubmit.setOnClickListener { handleSubmitButton() }

        return root
    }

    private fun handleSubmitButton() {
        val invitationCode = binding.wgJoinInvitationCodeEditText.text.toString()
        if (invitationCode.length != INVITATION_CODE_LENGTH) {
            showToast("The code is not correct.")
            return
        }
        // Disable button to prevent multiple requests
        binding.wgJoinButtonSubmit.isEnabled = false
        joinWgRequest(invitationCode)
    }

    private fun handleJoinWgResponse(response: Wg) {
        val mainViewModel =
            ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mainViewModel.wgCurrent.value = response
        mainViewModel.updateWgList(requireActivity())
        findNavController().navigate(R.id.homeFragment)
    }

    private fun joinWgRequest(invitationCode: String) {
        val token = TokenUtil.getTokenByActivity(requireActivity())
        val wgService = WgService(token)

        CoroutineScope(Dispatchers.IO).launch {
            val response = wgService.joinWg(invitationCode)
            withContext(Dispatchers.Main) {
                try {
                    Log.d(TAG, "joinWgRequest response: $response")
                    if (response != null) {
                        handleJoinWgResponse(response)
                    } else {
                        showToast("Sorry something went wrong.")
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "Exception ${e.message}")
                } catch (e: Throwable) {
                    Log.e(TAG, e.toString())
                }
                binding.wgJoinButtonSubmit.isEnabled = true
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
