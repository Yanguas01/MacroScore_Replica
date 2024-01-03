package es.upm.macroscore.presentation.home.feed.meal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentMealDialogBinding

@AndroidEntryPoint
class MealDialogFragment : DialogFragment() {

    private val viewModel by viewModels<MealDialogViewModel>()

    private var _binding: FragmentMealDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.buttonClose.setOnClickListener { super.dismiss() }
        binding.buttonSave.setOnClickListener {
            viewModel.saveMeal(binding.textInputEditText.text.toString(), binding.checkboxSaveMeal.isChecked)
            super.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}