package es.upm.macroscore.presentation.auth.signup

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.core.extensions.onTextChanged
import es.upm.macroscore.databinding.FragmentSignupBinding
import es.upm.macroscore.presentation.auth.AuthViewModel
import es.upm.macroscore.presentation.auth.AuthViewState
import es.upm.macroscore.presentation.states.NoValidationState
import es.upm.macroscore.presentation.states.OnlineValidationState
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupFragment : Fragment() {

    private val viewModel by activityViewModels<AuthViewModel>()

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

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
        initTextFields()
        initButtons()
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authViewState.collect { authViewState ->
                    setFieldsStates(authViewState)
                }
            }
        }
    }

    private fun initTextFields() {
        binding.editTextUsername.onTextChanged { text ->
            viewModel.validateUsername(text)
        }
        binding.editTextEmail.onTextChanged { text ->
            viewModel.validateEmail(text)
        }
        binding.editTextPassword.onTextChanged { text ->
            viewModel.validatePassword(text)
        }
        binding.editTextRepeatedPassword.onTextChanged { text ->
            viewModel.validateRepeatedPassword(binding.editTextPassword.text.toString(), text)
        }
    }

    private fun setFieldsStates(authViewState: AuthViewState) {
        setUsernameState(authViewState.usernameState)
        setEmailState(authViewState.emailState)
        setPasswordState(authViewState.passwordState)
        setRepeatedPasswordState(authViewState.repeatedPasswordState)
    }

    private fun setUsernameState(state: OnlineValidationState) {
        when (state) {
            is OnlineValidationState.Idle -> {
                binding.textInputLayoutUsername.error = null
                (binding.textInputLayoutUsername.endIconDrawable as? Animatable)?.stop()
                binding.textInputLayoutUsername.endIconMode = TextInputLayout.END_ICON_NONE
            }
            is OnlineValidationState.Loading -> {
                binding.textInputLayoutUsername.error = null
                binding.textInputLayoutUsername.endIconMode = TextInputLayout.END_ICON_CUSTOM
                (binding.textInputLayoutUsername.endIconDrawable as? Animatable)?.start()
            }
            is OnlineValidationState.Invalid -> {
                binding.textInputLayoutUsername.error = state.message
            }
            is OnlineValidationState.Success -> {
                (binding.textInputLayoutUsername.endIconDrawable as? Animatable)?.stop()
                binding.textInputLayoutUsername.endIconMode = TextInputLayout.END_ICON_NONE
                if (!state.status) binding.textInputLayoutUsername.error = "El nombre de usuario ya esta asociado a una cuenta"
            }
            is OnlineValidationState.Error -> {
                binding.textInputLayoutUsername.error = state.message
            }
        }
    }

    private fun setEmailState(state: OnlineValidationState) {
        when (state) {
            is OnlineValidationState.Idle -> {
                binding.textInputLayoutEmail.error = null
                (binding.textInputLayoutEmail.endIconDrawable as? Animatable)?.stop()
                binding.textInputLayoutEmail.endIconMode = TextInputLayout.END_ICON_NONE
            }
            is OnlineValidationState.Loading -> {
                binding.textInputLayoutEmail.error = null
                binding.textInputLayoutEmail.endIconMode = TextInputLayout.END_ICON_CUSTOM
                (binding.textInputLayoutEmail.endIconDrawable as? Animatable)?.start()
            }
            is OnlineValidationState.Invalid -> {
                binding.textInputLayoutEmail.error = state.message
            }
            is OnlineValidationState.Success -> {
                (binding.textInputLayoutEmail.endIconDrawable as? Animatable)?.stop()
                binding.textInputLayoutEmail.endIconMode = TextInputLayout.END_ICON_NONE
                if (!state.status) binding.textInputLayoutEmail.error = "La dirección de correo electrónico ya esta asociado a una cuenta"
            }
            is OnlineValidationState.Error -> {
                binding.textInputLayoutEmail.error = state.message
            }
        }
    }

    private fun setPasswordState(state: NoValidationState) {
        when (state) {
            is NoValidationState.Idle -> {
                binding.textInputLayoutPassword.error = null
            }
            is NoValidationState.Invalid -> {
                binding.textInputLayoutPassword.error = state.message
            }
            is NoValidationState.Valid -> {
                binding.textInputLayoutPassword.error = null
            }
        }
    }

    private fun setRepeatedPasswordState(state: NoValidationState) {
        when (state) {
            is NoValidationState.Idle -> {
                binding.textInputLayoutRepeatedPassword.error = null
            }
            is NoValidationState.Invalid -> {
                binding.textInputLayoutRepeatedPassword.error = state.message
            }
            is NoValidationState.Valid -> {
                binding.textInputLayoutRepeatedPassword.error = null
            }
        }
    }

    private fun initButtons() {
        binding.buttonNext.setOnClickListener {
            if (viewModel.isAbleToNav()
            ) {
                val direction = SignupFragmentDirections.actionSignupFragmentToProfileFormFragment()
                val extras = FragmentNavigatorExtras(
                    binding.containerLayout to "container_transition"
                )
                findNavController().navigate(direction, extras)
            }
        }
        binding.buttonLogin.setOnClickListener {
            val direction = SignupFragmentDirections.actionSignupFragmentToLoginFragment()
            val extras = FragmentNavigatorExtras(
                binding.containerLayout to "container_transition"
            )
            findNavController().navigate(direction, extras)
        }
    }
}