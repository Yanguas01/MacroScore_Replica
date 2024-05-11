package es.upm.macroscore.presentation

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.upm.macroscore.databinding.FragmentBottomSheetEditProfileBinding

class EditBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetEditProfileBinding? = null
    private val binding get() = _binding!!

    var onAcceptWithResponse: ((() -> Unit) -> Unit)? = null

    private var onAcceptAction: (() -> Unit)? = null
    private var onCancelAction: (() -> Unit)? = null

    class Builder {
        private var title: String? = null
        private var text: String? = null
        private var inputType: Int? = null
        private var hint: Int? = null
        private var startIcon: Int? = null
        private var endIcon: Int? = null
        private var prefix: String? = null
        private var suffix: String? = null
        private var onAcceptAction: (() -> Unit)? = null
        private var onCancelAction: (() -> Unit)? = null

        fun setTitle(title: String) = apply { this.title = title }
        fun setText(text: String?) = apply { this.text = text }
        fun setInputType(inputType: Int) = apply { this.inputType = inputType }
        fun setHint(hint: Int) = apply { this.hint = hint }
        fun setStartIcon(startIcon: Int) = apply { this.startIcon = startIcon }
        fun setEndIcon(endIcon: Int) = apply { this.endIcon = endIcon }
        fun setPrefix(prefix: String) = apply { this.prefix = prefix }
        fun setSuffix(suffix: String) = apply { this.suffix = suffix }
        fun setOnAcceptAction(action: () -> Unit) = apply { this.onAcceptAction = action }
        fun setOnCancelAction(action: () -> Unit) = apply { this.onCancelAction = action }

        fun build(): EditBottomSheet {
            val fragment = EditBottomSheet()
            fragment.arguments = Bundle().apply {
                putString("title", title)
                putString("text", text)
                inputType?.let { putInt("inputType", it) }
                hint?.let { putInt("hint", it) }
                startIcon?.let {
                    Log.d("hola mamawebaso", "hola mamawebaso")
                    putInt("startIcon", it)
                }
                endIcon?.let { putInt("endIcon", it) }
                putString("pefix", prefix)
                putString("suffix", suffix)
            }
            fragment.onAcceptAction = this.onAcceptAction
            fragment.onCancelAction = this.onCancelAction
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI(view)
    }

    private fun initUI(view: View) {
        view.post {
            initTitle()
            initTextInputLayout()
            initButtons()
        }
    }

    private fun initTitle() {
        arguments?.getString("title")?.let {
            binding.textViewTitle.text = it
        }
    }

    private fun initTextInputLayout() {
        arguments?.getString("text")?.let {
            binding.editText.setText(it)
        }
        arguments?.getInt("inputType")?.let {
            binding.editText.inputType = it
        }
        arguments?.getInt("hint")?.let {
            binding.textInputLayoutCopy.hint = requireContext().getString(it)
        }
        arguments?.getInt("startIcon")?.let {
            if (it != 0) {
                binding.textInputLayoutCopy.startIconDrawable =
                    AppCompatResources.getDrawable(requireContext(), it)
            }
        }
        arguments?.getInt("endIcon")?.let {
            if (it != 0) {
                binding.textInputLayoutCopy.endIconDrawable =
                    AppCompatResources.getDrawable(requireContext(), it)
            }
        }
        arguments?.getString("prefix")?.let {
            binding.textInputLayoutCopy.prefixText = it
        }
        arguments?.getString("suffix")?.let {
            binding.textInputLayoutCopy.suffixText = it
        }
    }

    private fun initButtons() {
        binding.buttonAccept.setOnClickListener {
            onAcceptWithResponse?.invoke {
                onAcceptAction?.invoke()
            }
        }
        binding.buttonCancel.setOnClickListener {
            onCancelAction?.invoke()
        }
    }

    fun startEndIconAnimation() {
        (binding.textInputLayoutCopy.endIconDrawable as? Animatable)?.start()
    }

    fun stopEndIconAnimation() {
        (binding.textInputLayoutCopy.endIconDrawable as? Animatable)?.stop()
    }
}
