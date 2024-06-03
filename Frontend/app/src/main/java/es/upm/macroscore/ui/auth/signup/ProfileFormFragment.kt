package es.upm.macroscore.ui.auth.signup

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
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.core.extensions.onTextChanged
import es.upm.macroscore.databinding.FragmentProfileFormBinding
import es.upm.macroscore.ui.home.HomeActivity
import es.upm.macroscore.ui.states.NoValidationFieldState
import es.upm.macroscore.ui.states.OnlineOperationState
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFormFragment : Fragment() {

    private val viewModel by activityViewModels<SignUpViewModel>()

    private var _binding: FragmentProfileFormBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        (binding.autocompleteTextViewPhysicalActivityLevel as? MaterialAutoCompleteTextView)?.setSimpleItems(
            R.array.physical_activity_level
        )
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
                viewModel.signup(
                    gender = binding.autocompleteTextViewGender.text.toString(),
                    physicalActivityLevel = binding.autocompleteTextViewPhysicalActivityLevel.text.toString(),
                    height = binding.editTextHeight.text.toString(),
                    weight = binding.editTextWeight.text.toString(),
                    age = binding.editTextAge.text.toString()
                )
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
                launch {
                    viewModel.signUpParamsState.collect { authViewState ->
                        setFieldsStates(authViewState)
                    }
                }
                launch {
                    viewModel.signUpActionState.collect { signUpActionState ->
                        when (signUpActionState) {
                            is OnlineOperationState.Idle -> { }
                            is OnlineOperationState.Loading -> {
                                binding.imageViewLoading.visibility = View.VISIBLE
                                (binding.imageViewLoading.drawable as? Animatable)?.start()
                            }
                            is OnlineOperationState.Success -> {
                                (binding.imageViewLoading.drawable as? Animatable)?.stop()
                                navigateToHome()
                            }
                            is OnlineOperationState.Error -> {
                                (binding.imageViewLoading.drawable as? Animatable)?.stop()
                                Toast.makeText(requireContext(), "Error de conexión, vuelva a intentarlo", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setFieldsStates(signUpParamsState: SignUpParamsState) {
        setGenderState(signUpParamsState.genderState)
        setPhysicalActivityLevelState(signUpParamsState.physicalActivityLevelState)
        setHeightState(signUpParamsState.heightState)
        setWeightState(signUpParamsState.weightState)
        setAgeState(signUpParamsState.ageState)
    }

    private fun setGenderState(state: NoValidationFieldState) {
        when (state) {
            is NoValidationFieldState.Idle -> {
                binding.textInputLayoutGender.error = null
            }

            is NoValidationFieldState.Invalid -> {
                binding.textInputLayoutGender.error = state.message
            }

            is NoValidationFieldState.Valid -> {
                binding.textInputLayoutGender.error = null
            }
        }
    }

    private fun setPhysicalActivityLevelState(state: NoValidationFieldState) {
        when (state) {
            is NoValidationFieldState.Idle -> {
                binding.textInputLayoutPhysicalActivityLevel.error = null
            }

            is NoValidationFieldState.Invalid -> {
                binding.textInputLayoutPhysicalActivityLevel.error = state.message
            }

            is NoValidationFieldState.Valid -> {
                binding.textInputLayoutPhysicalActivityLevel.error = null
            }
        }
    }

    private fun setHeightState(state: NoValidationFieldState) {
        when (state) {
            is NoValidationFieldState.Idle -> {
                binding.textInputLayoutHeight.error = null
            }

            is NoValidationFieldState.Invalid -> {
                binding.textInputLayoutHeight.error = state.message
            }

            is NoValidationFieldState.Valid -> {
                binding.textInputLayoutHeight.error = null
            }
        }
    }

    private fun setWeightState(state: NoValidationFieldState) {
        when (state) {
            is NoValidationFieldState.Idle -> {
                binding.textInputLayoutWeight.error = null
            }

            is NoValidationFieldState.Invalid -> {
                binding.textInputLayoutWeight.error = state.message
            }

            is NoValidationFieldState.Valid -> {
                binding.textInputLayoutWeight.error = null
            }
        }
    }

    private fun setAgeState(state: NoValidationFieldState) {
        when (state) {
            is NoValidationFieldState.Idle -> {
                binding.textInputLayoutAge.error = null
            }

            is NoValidationFieldState.Invalid -> {
                binding.textInputLayoutAge.error = state.message
            }

            is NoValidationFieldState.Valid -> {
                binding.textInputLayoutAge.error = null
            }
        }
    }
}