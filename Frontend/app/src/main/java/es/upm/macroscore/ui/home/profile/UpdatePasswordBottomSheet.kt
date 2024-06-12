package es.upm.macroscore.ui.home.profile

import android.graphics.drawable.Animatable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.upm.macroscore.R
import es.upm.macroscore.core.extensions.onTextChanged
import es.upm.macroscore.databinding.FragmentUpdatePasswordBottomSheetBinding
import es.upm.macroscore.ui.states.OnlineOperationState
import kotlinx.coroutines.launch


class UpdatePasswordBottomSheet : BottomSheetDialogFragment() {

    private val viewModel by activityViewModels<ProfileViewModel>()

    private var _binding: FragmentUpdatePasswordBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentUpdatePasswordBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initUIState()
    }

    private fun initUI() {
        initTextFields()
        initButtons()
    }

    private fun initTextFields() {
        binding.textInputOldPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.textInputOldPassword.error = null
        }
        binding.editTextOldPassword.onTextChanged {
            binding.textInputOldPassword.error = null
        }
        binding.textInputNewPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.textInputNewPassword.error = null
        }
        binding.editTextNewPassword.onTextChanged {
            binding.textInputNewPassword.error = null
        }
        binding.textInputLayoutRepeatedPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.textInputLayoutRepeatedPassword.error = null
        }
        binding.editTextRepeatedPassword.onTextChanged {
            binding.textInputLayoutRepeatedPassword.error = null
        }
    }

    private fun initButtons() {
        binding.buttonAccept.isEnabled =
            binding.textInputOldPassword.error == null &&
                    binding.textInputNewPassword.error == null &&
                    binding.textInputLayoutRepeatedPassword.error == null &&
                    binding.editTextOldPassword.text != null &&
                    binding.editTextNewPassword.text != null &&
                    binding.editTextRepeatedPassword.text != null

        binding.buttonAccept.setOnClickListener {
            viewModel.changePassword(
                binding.editTextOldPassword.text.toString(),
                binding.editTextNewPassword.text.toString(),
                binding.editTextRepeatedPassword.text.toString()
            )
        }
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updatePasswordBottomSheetState.collect { state ->
                    handleState(state)
                }
            }
        }
    }

    private fun handleState(state: OnlineOperationState) {
        val loadingDrawable =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_animated_loading)

        when (state) {
            is OnlineOperationState.Idle -> {
                binding.textInputOldPassword.error = null
                binding.textInputNewPassword.error = null
                binding.textInputLayoutRepeatedPassword.error = null

                binding.editTextOldPassword.text = null
                binding.editTextNewPassword.text = null
                binding.editTextRepeatedPassword.text = null

                binding.buttonAccept.icon = null
            }

            is OnlineOperationState.Loading -> {
                binding.buttonAccept.icon = loadingDrawable
                (binding.buttonAccept.icon as Animatable).start()
            }

            is OnlineOperationState.Success -> {
                (binding.buttonAccept.icon as Animatable).stop()
                binding.buttonAccept.icon = null
                dismiss()
            }
            is OnlineOperationState.Error -> {
                if (binding.buttonAccept.icon != null) {
                    (binding.buttonAccept.icon as Animatable).stop()
                    binding.buttonAccept.icon = null
                }

                when (state.errorId) {
                    0x00000001 -> { binding.textInputNewPassword.error = state.message }
                    0x00000002 -> { binding.textInputLayoutRepeatedPassword.error = state.message }
                    0x00000003 -> { binding.textInputOldPassword.error = state.message }
                }
            }
        }
    }
}