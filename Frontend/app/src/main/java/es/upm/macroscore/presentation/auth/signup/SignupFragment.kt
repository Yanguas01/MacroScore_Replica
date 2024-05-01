package es.upm.macroscore.presentation.auth.signup

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
        initButtons()
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

    private fun initButtons() {
        binding.buttonNext.setOnClickListener {
            if (viewModel.isAbleToNav(
                    binding.editTextUsername.text.toString(),
                    binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString(),
                    binding.editTextRepeatedPassword.text.toString()
                )
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

    /* SIN TRANSICIÓN
    private fun initButtons() {
        binding.buttonNext.setOnClickListener {
            if (viewModel.isAbleToNav(
                    binding.editTextUsername.text.toString(),
                    binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString(),
                    binding.editTextRepeatedPassword.text.toString()
                )
            ) findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToProfileFormFragment())
        }
        binding.buttonLogin.setOnClickListener {
            val direction = SignupFragmentDirections.actionSignupFragmentToLoginFragment()
            val extras = FragmentNavigatorExtras(
                binding.containerLayout to "container_transition"
            )
            findNavController().navigate(direction, extras)
        }
    }*/
}