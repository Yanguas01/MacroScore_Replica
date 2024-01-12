package es.upm.macroscore.presentation.auth.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.databinding.FragmentProfileFormBinding
import es.upm.macroscore.presentation.auth.AuthViewModel
import es.upm.macroscore.presentation.auth.AuthViewState
import es.upm.macroscore.presentation.home.HomeActivity
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFormFragment : Fragment() {

    private val viewModel by activityViewModels<AuthViewModel>()

    private var _binding: FragmentProfileFormBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileFormBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initUIState()
        binding.buttonSignup.setOnClickListener {
            if (viewModel.isAbleToSignUp(
                binding.autocompleteTextViewGender.text.toString(),
                binding.autocompleteTextViewPhysicalActivityLevel.text.toString(),
                binding.editTextHeight.text.toString(),
                binding.editTextWeight.text.toString(),
                binding.editTextAge.text.toString()
            )) {
                binding.circularProgressBar.visibility = View.VISIBLE
                navigateToHome()
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
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
        binding.textInputLayoutGender.error =
            if (authViewState.genderError != null) authViewState.genderError else null
        binding.textInputLayoutPhysicalActivityLevel.error =
            if (authViewState.physicalActivityLevelError != null) authViewState.physicalActivityLevelError else null
        binding.textInputLayoutHeight.error =
            if (authViewState.heightError != null) authViewState.heightError else null
        binding.textInputLayoutWeight.error =
            if (authViewState.weightError != null) authViewState.weightError else null
        binding.textInputLayoutAge.error =
            if (authViewState.ageError != null) authViewState.ageError else null
    }
}