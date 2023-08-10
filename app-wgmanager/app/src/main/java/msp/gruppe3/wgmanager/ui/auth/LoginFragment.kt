package msp.gruppe3.wgmanager.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.databinding.FragmentLoginBinding
import msp.gruppe3.wgmanager.models.dtos.LoginDto
import msp.gruppe3.wgmanager.services.AuthService
import org.apache.commons.validator.routines.EmailValidator

class LoginFragment : Fragment() {
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView


    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.hide()

        val root: View = binding.root

        // initialize UI components

        emailInput = binding.loginEmail
        passwordInput = binding.loginPassword
        loginButton = binding.loginButton
        registerLink = binding.registerHere

        loginButton.setOnClickListener { handleSubmit() }
        registerLink.setOnClickListener { switchToRegister() }
        return root
    }

    private fun handleSubmit() {
        val emailText = emailInput.text.toString()
        val passwordText = passwordInput.text.toString()
        val validEmail = EmailValidator.getInstance().isValid(emailText)
        val validPassword = passwordText.isNotBlank()

        if (validEmail && validPassword) {
            loginRequest(LoginDto(emailText, passwordText))
        }
    }

    private fun switchToRegister() {
        this.findNavController().navigate(R.id.registerFragment)
    }

    private fun loginRequest(loginDto: LoginDto) {
        val authService = AuthService()
        CoroutineScope(Dispatchers.IO).launch {
            val response = authService.login(loginDto)
            if (response?.token?.isNotBlank() == true) {
                activity?.getSharedPreferences("wgManagerPref", Context.MODE_PRIVATE)?.edit()
                    ?.putString("token", response.token)?.apply()
                activity?.runOnUiThread {
                    findNavController().navigate(R.id.mainActivity)
                }

            }
        }
    }

}