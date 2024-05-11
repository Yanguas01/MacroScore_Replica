package es.upm.macroscore.presentation.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentProfileBinding
import es.upm.macroscore.presentation.EditBottomSheet
import es.upm.macroscore.presentation.home.profile.enums.TextInputLayoutInfo

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
        binding.buttonEditUsername.setOnClickListener {initBottomSheet(TextInputLayoutInfo.Username) }
        binding.buttonEditEmail.setOnClickListener { initBottomSheet(TextInputLayoutInfo.Email) }
        binding.buttonEditGender.setOnClickListener { }
        binding.buttonEditPhysicalActivityLevel.setOnClickListener { }
        binding.buttonEditHeight.setOnClickListener { initBottomSheet(TextInputLayoutInfo.Height) }
        binding.buttonEditWeight.setOnClickListener {initBottomSheet(TextInputLayoutInfo.Weight) }
        binding.buttonEditAge.setOnClickListener {initBottomSheet(TextInputLayoutInfo.Age) }
    }

    private fun initBottomSheet(textInputLayoutInfo: TextInputLayoutInfo) {
        val bottomSheet = EditBottomSheet.Builder()
            .setTitle(requireContext().getString(R.string.title_edit_profile, requireContext().getString(textInputLayoutInfo.hint)))
            .setInputType(textInputLayoutInfo.inputType)
            .setHint(textInputLayoutInfo.hint)
            .setOnAcceptAction {  } // TODO Cambiar
            .setOnCancelAction {  } // TODO Cambiar
            .build()

        bottomSheet.show(requireActivity().supportFragmentManager, "bottom_sheet")
    }
}