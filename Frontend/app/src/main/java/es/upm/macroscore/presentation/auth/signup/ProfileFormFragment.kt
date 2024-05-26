package es.upm.macroscore.presentation.auth.signup

import android.content.Intent
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.core.extensions.onTextChanged
import es.upm.macroscore.databinding.FragmentProfileFormBinding
import es.upm.macroscore.presentation.auth.AuthViewModel
import es.upm.macroscore.presentation.auth.AuthViewState
import es.upm.macroscore.presentation.home.HomeActivity
import es.upm.macroscore.presentation.states.NoValidationState
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFormFragment : Fragment() {

    private val viewModel by activityViewModels<AuthViewModel>()

    private var _binding: FragmentProfileFormBinding? = null
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
        _binding = FragmentProfileFormBinding.inflate(layoutInflater, container, false)
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

    private fun initTextFields() {
        (binding.autocompleteTextViewGender as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.gender)
        binding.autocompleteTextViewGender.onTextChanged { text ->
            viewModel.validateGender(text)
        }
        (binding.autocompleteTextViewPhysicalActivityLevel as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.physical_activity_level)
        binding.autocompleteTextViewPhysicalActivityLevel.onTextChanged { text ->
            viewModel.validatePhysicalActivityLevel(text)
        }
        binding.editTextHeight.onTextChanged { text ->
            viewModel.validateHeight(text)
        }
        binding.editTextWeight.onTextChanged { text ->
            viewModel.validateWeight(text)
        }
        binding.editTextAge.onTextChanged { text ->
            viewModel.validateAge(text)
        }
    }

    private fun initButtons() {
        binding.buttonGetBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        binding.buttonSignup.setOnClickListener {
            if (viewModel.isAbleToSignUp()) {
                binding.imageViewLoading.visibility = View.VISIBLE
                (binding.imageViewLoading as? Animatable)?.start()
                viewModel.signup()
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
                    setFieldsStates(authViewState)
                }
            }
        }
    }

    private fun setFieldsStates(authViewState: AuthViewState) {
        setGenderState(authViewState.genderState)
        setPhysicalActivityLevelState(authViewState.physicalActivityLevelState)
        setHeightState(authViewState.heightState)
        setWeightState(authViewState.weightState)
        setAgeState(authViewState.ageState)
    }

    private fun setGenderState(state: NoValidationState) {
        when (state) {
            is NoValidationState.Idle -> {
                binding.textInputLayoutGender.error = null
            }

            is NoValidationState.Invalid -> {
                binding.textInputLayoutGender.error = state.message
            }

            is NoValidationState.Valid -> {
                binding.textInputLayoutGender.error = null
            }
        }
    }

    private fun setPhysicalActivityLevelState(state: NoValidationState) {
        when (state) {
            is NoValidationState.Idle -> {
                binding.textInputLayoutPhysicalActivityLevel.error = null
            }

            is NoValidationState.Invalid -> {
                binding.textInputLayoutPhysicalActivityLevel.error = state.message
            }

            is NoValidationState.Valid -> {
                binding.textInputLayoutPhysicalActivityLevel.error = null
            }
        }
    }

    private fun setHeightState(state: NoValidationState) {
        when (state) {
            is NoValidationState.Idle -> {
                binding.textInputLayoutHeight.error = null
            }

            is NoValidationState.Invalid -> {
                binding.textInputLayoutHeight.error = state.message
            }

            is NoValidationState.Valid -> {
                binding.textInputLayoutHeight.error = null
            }
        }
    }

    private fun setWeightState(state: NoValidationState) {
        when (state) {
            is NoValidationState.Idle -> {
                binding.textInputLayoutWeight.error = null
            }

            is NoValidationState.Invalid -> {
                binding.textInputLayoutWeight.error = state.message
            }

            is NoValidationState.Valid -> {
                binding.textInputLayoutWeight.error = null
            }
        }
    }

    private fun setAgeState(state: NoValidationState) {
        when (state) {
            is NoValidationState.Idle -> {
                binding.textInputLayoutAge.error = null
            }

            is NoValidationState.Invalid -> {
                binding.textInputLayoutAge.error = state.message
            }

            is NoValidationState.Valid -> {
                binding.textInputLayoutAge.error = null
            }
        }
    }

}