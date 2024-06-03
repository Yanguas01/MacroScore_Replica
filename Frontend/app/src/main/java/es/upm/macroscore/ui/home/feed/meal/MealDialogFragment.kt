package es.upm.macroscore.ui.home.feed.meal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentMealDialogBinding
import es.upm.macroscore.ui.home.feed.FeedViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MealDialogFragment : DialogFragment() {

    private val viewModel by viewModels<FeedViewModel>()

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
        initUIState()
        initToolbar()
        initCommonMeals()
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isReadyToDismiss.collect {
                        super.dismiss()
                    }
                }
            }
        }
    }

    private fun initToolbar() {
        binding.buttonClose.setOnClickListener { super.dismiss() }
        binding.buttonSave.setOnClickListener {
            viewModel.addMeal(binding.textInputEditText.text.toString(), binding.checkboxSaveMeal.isChecked)
        }
    }

    private fun initCommonMeals() {
        binding.buttonBreakfast.setOnClickListener {
            binding.textInputEditText.setText(binding.buttonBreakfast.text)
        }
        binding.buttonBrunch.setOnClickListener {
            binding.textInputEditText.setText(binding.buttonBrunch.text)
        }
        binding.buttonLunch.setOnClickListener {
            binding.textInputEditText.setText(binding.buttonLunch.text)
        }
        binding.buttonAfternoonSnack.setOnClickListener {
            binding.textInputEditText.setText(binding.buttonAfternoonSnack.text)
        }
        binding.buttonDinner.setOnClickListener {
            binding.textInputEditText.setText(binding.buttonDinner.text)
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