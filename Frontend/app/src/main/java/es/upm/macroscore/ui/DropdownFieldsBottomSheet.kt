package es.upm.macroscore.ui

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import es.upm.macroscore.core.extensions.onTextChanged
import es.upm.macroscore.databinding.FragmentBottomSheetDropdownFieldsBinding
import es.upm.macroscore.databinding.FragmentBottomSheetEditProfileBinding

class DropdownFieldsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetDropdownFieldsBinding? = null
    private val binding get() = _binding!!

    private var startIcon: Int? = null
    private var loadingButtonIcon: Int? = null
    private var onAcceptAction: ((bottomSheet: DropdownFieldsBottomSheet) -> Unit)? = null
    private var onCancelAction: ((bottomSheet: DropdownFieldsBottomSheet) -> Unit)? = null
    private var onTextChangedAction: ((String, bottomSheet: DropdownFieldsBottomSheet) -> Unit)? = null

    class Builder(private val fragmentManager: FragmentManager) {
        private val bottomSheet = DropdownFieldsBottomSheet()
        private val args = Bundle()

        fun setTitle(title: String) = apply { args.putString("title", title) }
        fun setText(text: String?) = apply { args.putString("text", text) }
        fun setSimpleItems(simpleItems: Int) = apply { args.putInt("simpleItems", simpleItems) }
        fun setInputType(inputType: Int) = apply { args.putInt("inputType", inputType) }
        fun setHint(hint: Int) = apply { args.putInt("hint", hint) }
        fun setStartIcon(startIcon: Int) = apply { args.putInt("startIcon", startIcon) }
        fun setLoadingButtonIcon(icon: Int) = apply { args.putInt("loadingButtonIcon", icon) }
        fun setOnAcceptAction(action: (bottomSheet: DropdownFieldsBottomSheet) -> Unit) =
            apply { bottomSheet.onAcceptAction = action }
        fun setOnCancelAction(action: (bottomSheet: DropdownFieldsBottomSheet) -> Unit) =
            apply { bottomSheet.onCancelAction = action }
        fun setOnTextChangedAction(action: (text: String, bottomSheet: DropdownFieldsBottomSheet) -> Unit) =
            apply { bottomSheet.onTextChangedAction = action }

        private fun build(): DropdownFieldsBottomSheet {
            bottomSheet.arguments = args
            return bottomSheet
        }

        fun show(): DropdownFieldsBottomSheet {
            val sheet = build()
            sheet.show(fragmentManager, EditBottomSheet::class.java.simpleName)
            return sheet
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetDropdownFieldsBinding.inflate(inflater, container, false)
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
        arguments?.getInt("simpleItems")?.let {
            binding.editText.setSimpleItems(it)
        }
        arguments?.getInt("hint")?.let {
            binding.textInputLayoutCopy.hint = requireContext().getString(it)
        }
        arguments?.getInt("startIcon")?.let {
            if (it != 0) startIcon = it
        }
        binding.editText.onTextChanged {
            binding.buttonAccept.isEnabled = if (it.isNotBlank() && it.trimEnd() != arguments?.getString("text")) {
                onTextChangedAction?.invoke(it, this)
                true
            } else {
                false
            }
        }
    }

    private fun initButtons() {
        binding.buttonAccept.isEnabled = false
        arguments?.getInt("loadingButtonIcon")?.let {
            if (it != 0) loadingButtonIcon = it
        }
        binding.buttonAccept.setOnClickListener {
            onAcceptAction?.invoke(this)
        }
        binding.buttonCancel.setOnClickListener {
            onCancelAction?.invoke(this)
        }
    }

    fun getText(): String {
        return binding.editText.text.toString()
    }

    fun getTextField(): TextInputLayout {
        return binding.textInputLayoutCopy
    }

    fun setTextFieldError(text: String?) {
        binding.textInputLayoutCopy.error = text
    }

    fun startEndIconAnimation() {
        binding.textInputLayoutCopy.endIconDrawable?.setVisible(true, true)
        (binding.textInputLayoutCopy.endIconDrawable as? Animatable)?.start()
    }

    fun stopEndIconAnimation() {
        (binding.textInputLayoutCopy.endIconDrawable as? Animatable)?.stop()
        binding.textInputLayoutCopy.endIconDrawable = null
    }

    fun startButtonIconAnimation() {
        binding.buttonAccept.icon =
            loadingButtonIcon?.let { AppCompatResources.getDrawable(requireContext(), it) }
        binding.buttonAccept.icon.setVisible(true, true)
        (binding.buttonAccept.icon as? Animatable)?.start()
    }

    fun stopButtonIconAnimation() {
        (binding.buttonAccept.icon as? Animatable)?.stop()
        binding.buttonAccept.icon = null
    }

    fun block(block: Boolean) {
        binding.editText.isEnabled = !block
        binding.buttonAccept.isEnabled = !block
        binding.buttonCancel.isEnabled = !block
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.editText.isEnabled) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}