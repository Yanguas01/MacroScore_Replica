package es.upm.macroscore.presentation.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentSignupBinding
import es.upm.macroscore.presentation.auth.AuthViewModel
import es.upm.macroscore.presentation.auth.AuthViewState
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupFragment : Fragment() {

    private val viewModel by activityViewModels<AuthViewModel>()

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initUIState()
        binding.buttonNext.setOnClickListener {
            if (viewModel.isAbleToNav(
                binding.editTextUsername.text.toString(),
                binding.editTextEmail.text.toString(),
                binding.editTextPassword.text.toString(),
                binding.editTextRepeatedPassword.text.toString()
            )) findNavController().navigate(R.id.action_signupFragment_to_profileFormFragment)
        }
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authViewState.collect { authViewState ->
                    setFieldsErrors(authViewState)
                }
            }
        }
    }

    private fun setFieldsErrors(authViewState: AuthViewState) {
        binding.textInputLayoutUsername.error =
            if (authViewState.usernameError != null) authViewState.usernameError else null
        binding.textInputLayoutEmail.error =
            if (authViewState.emailError != null) authViewState.emailError else null
        binding.textInputLayoutPassword.error =
            if (authViewState.passwordError != null) authViewState.passwordError else null
        binding.textInputLayoutRepeatedPassword.error =
            if (authViewState.passwordConfirmedError != null) authViewState.passwordConfirmedError else null
    }
}