package msp.gruppe3.wgmanager.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.databinding.FragmentRegisterBinding
import msp.gruppe3.wgmanager.models.dtos.RegisterDto
import msp.gruppe3.wgmanager.services.AuthService
import org.apache.commons.validator.routines.EmailValidator

class RegisterFragment : Fragment() {
    private lateinit var username: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var loginLink: TextView

    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as AppCompatActivity).supportActionBar?.hide()

        // initialize UI components

        username = binding.username
        email = binding.registerEmail
        password = binding.registerPassword
        confirmPassword = binding.confirmPassword
        registerButton = binding.registerButton
        loginLink = binding.loginHere

        registerButton.setOnClickListener { handleSubmit() }
        loginLink.setOnClickListener { switchToLogIn() }
        return root
    }


    private fun handleSubmit() {
        val usernameText = username.text.toString()
        val emailText = email.text.toString()
        val passwordText = password.text.toString()
        val passwordConfirmationText = confirmPassword.text.toString()
        val validEmail = EmailValidator.getInstance().isValid(emailText)
        val validPassword = passwordText == passwordConfirmationText
        val completedData =
            usernameText.isNotBlank() && passwordText.isNotBlank() && passwordConfirmationText.isNotBlank()

        if (validEmail && validPassword && completedData) {
            registerRequest(RegisterDto(emailText, passwordText, usernameText))
        }
    }

    private fun registerRequest(registerDto: RegisterDto) {
        val authService = AuthService()
        CoroutineScope(Dispatchers.IO).launch {
            val response = authService.register(registerDto)
            if (response?.id != null) {
                switchToLogIn()
            }
        }
    }

    private fun switchToLogIn() {
        activity?.runOnUiThread {
            this.findNavController().navigate(R.id.loginFragment)
        }
    }
}