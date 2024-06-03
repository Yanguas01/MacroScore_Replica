package es.upm.macroscore.ui.auth.signup

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
import es.upm.macroscore.ui.states.NoValidationFieldState
import es.upm.macroscore.ui.states.OnlineValidationFieldState
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val viewModel by activityViewModels<SignUpViewModel>()

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
                viewModel.signUpParamsState.collect { authViewState ->
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

    private fun setFieldsStates(signUpParamsState: SignUpParamsState) {
        setUsernameState(signUpParamsState.usernameState)
        setEmailState(signUpParamsState.emailState)
        setPasswordState(signUpParamsState.passwordState)
        setRepeatedPasswordState(signUpParamsState.repeatedPasswordState)
    }

    private fun setUsernameState(state: OnlineValidationFieldState) {
        when (state) {
            is OnlineValidationFieldState.Idle -> {
                binding.textInputLayoutUsername.error = null
                (binding.textInputLayoutUsername.endIconDrawable as? Animatable)?.stop()
                binding.textInputLayoutUsername.endIconMode = TextInputLayout.END_ICON_NONE
            }

            is OnlineValidationFieldState.Loading -> {
                binding.textInputLayoutUsername.error = null
                binding.textInputLayoutUsername.endIconMode = TextInputLayout.END_ICON_CUSTOM
                (binding.textInputLayoutUsername.endIconDrawable as? Animatable)?.start()
            }

            is OnlineValidationFieldState.Invalid -> {
                binding.textInputLayoutUsername.error = state.message
            }

            is OnlineValidationFieldState.Success -> {
                (binding.textInputLayoutUsername.endIconDrawable as? Animatable)?.stop()
                binding.textInputLayoutUsername.endIconMode = TextInputLayout.END_ICON_NONE
                if (!state.status) binding.textInputLayoutUsername.error =
                    "El nombre de usuario ya esta asociado a una cuenta"
            }

            is OnlineValidationFieldState.Error -> {
                binding.textInputLayoutUsername.error = state.message
            }
        }
    }

    private fun setEmailState(state: OnlineValidationFieldState) {
        when (state) {
            is OnlineValidationFieldState.Idle -> {
                binding.textInputLayoutEmail.error = null
                (binding.textInputLayoutEmail.endIconDrawable as? Animatable)?.stop()
                binding.textInputLayoutEmail.endIconMode = TextInputLayout.END_ICON_NONE
            }

            is OnlineValidationFieldState.Loading -> {
                binding.textInputLayoutEmail.error = null
                binding.textInputLayoutEmail.endIconMode = TextInputLayout.END_ICON_CUSTOM
                (binding.textInputLayoutEmail.endIconDrawable as? Animatable)?.start()
            }

            is OnlineValidationFieldState.Invalid -> {
                binding.textInputLayoutEmail.error = state.message
            }

            is OnlineValidationFieldState.Success -> {
                (binding.textInputLayoutEmail.endIconDrawable as? Animatable)?.stop()
                binding.textInputLayoutEmail.endIconMode = TextInputLayout.END_ICON_NONE
                if (!state.status) binding.textInputLayoutEmail.error =
                    "La dirección de correo electrónico ya esta asociado a una cuenta"
            }

            is OnlineValidationFieldState.Error -> {
                binding.textInputLayoutEmail.error = state.message
            }
        }
    }

    private fun setPasswordState(state: NoValidationFieldState) {
        when (state) {
            is NoValidationFieldState.Idle -> {
                binding.textInputLayoutPassword.error = null
            }

            is NoValidationFieldState.Invalid -> {
                binding.textInputLayoutPassword.error = state.message
            }

            is NoValidationFieldState.Valid -> {
                binding.textInputLayoutPassword.error = null
            }
        }
    }

    private fun setRepeatedPasswordState(state: NoValidationFieldState) {
        when (state) {
            is NoValidationFieldState.Idle -> {
                binding.textInputLayoutRepeatedPassword.error = null
            }

            is NoValidationFieldState.Invalid -> {
                binding.textInputLayoutRepeatedPassword.error = state.message
            }

            is NoValidationFieldState.Valid -> {
                binding.textInputLayoutRepeatedPassword.error = null
            }
        }
    }

    private fun initButtons() {
        binding.buttonNext.setOnClickListener {
            if (viewModel.isAbleToNav(
                    binding.editTextUsername.text.toString(),
                    binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString()
                )
            ) {
                val direction = SignUpFragmentDirections.actionSignUpFragmentToProfileFormFragment()
                val extras = FragmentNavigatorExtras(
                    binding.containerLayout to "container_transition"
                )
                findNavController().navigate(direction, extras)
            }
        }
        binding.buttonLogin.setOnClickListener {
            val direction = SignUpFragmentDirections.actionSignUpFragmentToLogInFragment()
            val extras = FragmentNavigatorExtras(
                binding.containerLayout to "container_transition"
            )
            findNavController().navigate(direction, extras)
        }
    }
}