package es.upm.macroscore.ui

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.upm.macroscore.databinding.FragmentBottomSheetEditProfileBinding

class EditBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetEditProfileBinding? = null
    private val binding get() = _binding!!

    var onAcceptWithResponse: ((() -> Unit) -> Unit)? = null

    private var onAcceptAction: ((bottomSheet: EditBottomSheet) -> Unit)? = null
    private var onCancelAction: ((bottomSheet: EditBottomSheet) -> Unit)? = null

    class Builder(private val fragmentManager: FragmentManager) {
        private val bottomSheet = EditBottomSheet()
        private val args = Bundle()

        fun setTitle(title: String) = apply { args.putString("title", title) }
        fun setText(text: String?) = apply { args.putString("text", text) }
        fun setInputType(inputType: Int) = apply { args.putInt("inputType", inputType) }
        fun setHint(hint: Int) = apply { args.putInt("hint", hint) }
        fun setStartIcon(startIcon: Int) = apply { args.putInt("startIcon", startIcon) }
        fun setEndIcon(endIcon: Int) = apply { args.putInt("endIcon", endIcon) }
        fun setPrefix(prefix: String) = apply { args.putString("prefix", prefix) }
        fun setSuffix(suffix: String) = apply { args.putString("suffix", suffix) }
        fun setOnAcceptAction(action: (bottomSheet: EditBottomSheet) -> Unit) = apply { bottomSheet.onAcceptAction = action }
        fun setOnCancelAction(action: (bottomSheet: EditBottomSheet) -> Unit) = apply { bottomSheet.onCancelAction = action }

        private fun build(): EditBottomSheet {
            bottomSheet.arguments = args
            return bottomSheet
        }

        fun show(): EditBottomSheet {
            val sheet = build()
            sheet.show(fragmentManager, EditBottomSheet::class.java.simpleName)
            return sheet
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
            onAcceptAction?.invoke(this)
        }
        binding.buttonCancel.setOnClickListener {
            onCancelAction?.invoke(this)
        }
    }

    fun startEndIconAnimation() {
        (binding.textInputLayoutCopy.endIconDrawable as? Animatable)?.start()
    }

    fun stopEndIconAnimation() {
        (binding.textInputLayoutCopy.endIconDrawable as? Animatable)?.stop()
    }
}
