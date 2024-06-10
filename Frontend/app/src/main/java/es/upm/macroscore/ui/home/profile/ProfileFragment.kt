package es.upm.macroscore.ui.home.profile

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentProfileBinding
import es.upm.macroscore.ui.EditBottomSheet
import es.upm.macroscore.ui.home.profile.enums.TextInputLayoutInfo

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initEditButtons()
    }

    private fun initEditButtons() {
        binding.buttonGeneralData.setOnClickListener { toggleSubButtons(binding.layoutButtonGeneralDataContainer, binding.buttonGeneralData) }
        binding.buttonPersonalData.setOnClickListener { toggleSubButtons(binding.layoutButtonPersonalDataContainer, binding.buttonPersonalData) }
        binding.buttonUsername.setOnClickListener { initBottomSheet(TextInputLayoutInfo.Username) }
        binding.buttonEmail.setOnClickListener { initBottomSheet(TextInputLayoutInfo.Email) }
        binding.buttonGender.setOnClickListener { }
        binding.buttonPhysicalActivityLevel.setOnClickListener { }
        binding.buttonHeight.setOnClickListener { initBottomSheet(TextInputLayoutInfo.Height) }
        binding.buttonWeight.setOnClickListener {initBottomSheet(TextInputLayoutInfo.Weight) }
        binding.buttonAge.setOnClickListener {initBottomSheet(TextInputLayoutInfo.Age) }
    }

    private fun toggleSubButtons(container: LinearLayoutCompat, button: MaterialButton) {
        if (container.visibility == View.VISIBLE) {
            container.animate().alpha(0f).setDuration(300).withEndAction {
                container.visibility = View.GONE
                rotateIcon(button, 90f, 0f)
            }.start()
        } else {
            container.visibility = View.VISIBLE
            container.alpha = 0f
            container.animate().alpha(1f).setDuration(300).start()
            rotateIcon(button, 0f, 90f)
        }
    }

    private fun rotateIcon(button: MaterialButton, fromDegrees: Float, toDegrees: Float) {
        val icon = button.icon
        icon?.let {
            val rotateAnimation = ObjectAnimator.ofFloat(it, "rotation", fromDegrees, toDegrees)
            rotateAnimation.duration = 300
            rotateAnimation.start()
        }
    }

    private fun initBottomSheet(textInputLayoutInfo: TextInputLayoutInfo) {
        val bottomSheet = EditBottomSheet.Builder(requireActivity().supportFragmentManager)
            .setTitle(requireContext().getString(R.string.title_edit_profile, requireContext().getString(textInputLayoutInfo.hint)))
            .setInputType(textInputLayoutInfo.inputType)
            .setHint(textInputLayoutInfo.hint)
            .setOnAcceptAction { bottomSheet ->
                bottomSheet.dismiss() // TODO Cambiar
            }
            .setOnCancelAction { bottomSheet ->
                bottomSheet.dismiss() // TODO Cambiar
            }
            .show()

    }
}