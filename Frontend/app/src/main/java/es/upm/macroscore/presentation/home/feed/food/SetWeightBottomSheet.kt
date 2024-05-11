package es.upm.macroscore.presentation.home.feed.food

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.upm.macroscore.core.extensions.onTextChanged
import es.upm.macroscore.databinding.FragmentBottomSheetSetFoodWeightBinding

class SetWeightBottomSheet(val addFood: () -> Unit): BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetSetFoodWeightBinding? = null
    private val binding get() = _binding!!

    private var weightError: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetSetFoodWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.editText.onTextChanged {
            updateButtonState(it)
        }
        initButtons()
    }

    private fun initButtons() {
        binding.buttonAccept.setOnClickListener {
            addFood()
            super.dismiss()
        }
        binding.buttonCancel.setOnClickListener {
            super.dismiss()
        }
    }

    private fun updateButtonState(weight: String) {
            weightError = try {
                if (weight.toDouble() <= 0) {
                    "Introduzca un peso válido"
                } else null
            } catch (e: NumberFormatException) {
                "Introduzca un peso válido"
            }
            updateState()
        }

    private fun updateState() {
        if (weightError == null) {
            binding.textInputLayoutCopy.error = null
            binding.buttonAccept.isEnabled = true
        } else {
            binding.textInputLayoutCopy.error = weightError
            binding.buttonAccept.isEnabled = false
        }
    }
}