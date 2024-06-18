package es.upm.macroscore.ui.auth.login

import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.core.extensions.onTextChanged
import es.upm.macroscore.databinding.FragmentLoginBinding
import es.upm.macroscore.ui.home.HomeActivity
import es.upm.macroscore.ui.states.NoValidationFieldState
import es.upm.macroscore.ui.states.OnlineOperationState
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LogInFragment : Fragment() {

    private val viewModel by activityViewModels<LogInViewModel>()

    private var _binding: FragmentLoginBinding? = null
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
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
                launch {
                    viewModel.logInParamsState.collect { logInViewState ->
                        setFieldsStates(logInViewState)
                    }
                }
                launch {
                    viewModel.logInActionState.collect { logInActionState ->
                        handleState(logInActionState)
                    }
                }
            }
        }
    }

    private fun setFieldsStates(logInViewState: LogInParamsState) {
        setUsernameTextField(logInViewState.usernameState)
        setPasswordTextField(logInViewState.passwordState)
    }

    private fun setUsernameTextField(state: NoValidationFieldState) {
        when (state) {
            is NoValidationFieldState.Idle -> {
                binding.textInputLayoutUsername.error = null
            }
            is NoValidationFieldState.Invalid -> {
                binding.textInputLayoutUsername.error = state.message
            }
            is NoValidationFieldState.Valid -> {
                binding.textInputLayoutUsername.error = null
            }
        }
    }

    private fun setPasswordTextField(state: NoValidationFieldState) {
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

    private fun handleState(logInActionState: OnlineOperationState) {
        when (logInActionState) {
            is OnlineOperationState.Idle -> { }
            is OnlineOperationState.Loading -> {
                binding.imageViewLoading.visibility = View.VISIBLE
                (binding.imageViewLoading.drawable as? Animatable)?.start()
            }
            is OnlineOperationState.Success -> {
                (binding.imageViewLoading.drawable as? Animatable)?.stop()
                binding.imageViewLoading.visibility = View.INVISIBLE
                navigateToHome()
            }
            is OnlineOperationState.Error -> {
                (binding.imageViewLoading.drawable as? Animatable)?.stop()
                binding.imageViewLoading.visibility = View.INVISIBLE
                if(logInActionState.errorId != LoginErrorCodes.ERROR_BAD_INPUT) Toast.makeText(requireContext(), "Error de conexión, vuelva a intentarlo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun initTextFields() {
        binding.editTextUsername.onTextChanged { text ->
            viewModel.validateUsername(text)
        }
        binding.editTextPassword.onTextChanged { text ->
            viewModel.validatePassword(text, binding.editTextUsername.text.toString())
        }
    }

    private fun initButtons() {
        binding.buttonSignup.setOnClickListener {
            val direction = LogInFragmentDirections.actionLogInFragmentToSignUpFragment()
            val extras = FragmentNavigatorExtras(
                binding.containerLayout to "container_transition"
            )
            findNavController().navigate(direction, extras)
        }
        binding.buttonLogin.setOnClickListener {
            if (viewModel.isAbleToSignUp()) {
                viewModel.logIn(
                    username = binding.editTextUsername.text.toString(),
                    password = binding.editTextPassword.text.toString(),
                    keepLoggedIn = binding.checkBoxKeepLoggedIn.isChecked
                )
            }
        }
    }
}