package es.upm.macroscore.ui.home.feed.meal

import android.content.res.ColorStateList
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import es.upm.macroscore.R
import es.upm.macroscore.core.extensions.onTextChanged
import es.upm.macroscore.databinding.FragmentMealDialogBinding
import es.upm.macroscore.ui.home.feed.FeedViewModel
import es.upm.macroscore.ui.states.OnlineOperationState
import kotlinx.coroutines.launch

class MealDialogFragment : DialogFragment() {

    private val viewModel by activityViewModels<FeedViewModel>()

    private var _binding: FragmentMealDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initUIState()
    }

    private fun initUI() {
        initToolbar()
        initTextInputLayout()
    }

    private fun initTextInputLayout() {
        binding.editTextMealName.onTextChanged {
            viewModel.resetMealDialogState()
        }
        initCommonMealsButtons()
    }

    private fun initToolbar() {
        binding.buttonClose.setOnClickListener {
            viewModel.resetMealDialogState()
            this.dismiss()
        }
        binding.buttonSave.setOnClickListener {
            viewModel.addMeal(
                binding.editTextMealName.text.toString(),
                binding.checkBoxSaveMeal.isChecked
            )
        }
    }

    private fun initCommonMealsButtons() {
        binding.buttonBreakfast.setOnClickListener {
            binding.editTextMealName.setText(binding.buttonBreakfast.text)
        }
        binding.buttonBrunch.setOnClickListener {
            binding.editTextMealName.setText(binding.buttonBrunch.text)
        }
        binding.buttonLunch.setOnClickListener {
            binding.editTextMealName.setText(binding.buttonLunch.text)
        }
        binding.buttonAfternoonSnack.setOnClickListener {
            binding.editTextMealName.setText(binding.buttonAfternoonSnack.text)
        }
        binding.buttonDinner.setOnClickListener {
            binding.editTextMealName.setText(binding.buttonDinner.text)
        }
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mealDialogState.collect { state ->
                    handleState(state)
                }
            }
        }
    }

    private fun handleState(state: OnlineOperationState) {
        when (state) {
            is OnlineOperationState.Idle -> {
                binding.textInputMealName.error = null
                binding.checkBoxSaveMeal.buttonTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.secondary
                    )
                )
                binding.checkBoxSaveMeal.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text
                    )
                )
                binding.imageViewCheckBoxError.visibility = View.GONE
                binding.textViewCheckBoxError.visibility = View.GONE
            }
            is OnlineOperationState.Loading -> {
                binding.imageViewLoading.visibility = View.VISIBLE
                (binding.imageViewLoading.drawable as? Animatable)?.start()
            }

            is OnlineOperationState.Success -> {
                (binding.imageViewLoading.drawable as? Animatable)?.stop()
                binding.imageViewLoading.visibility = View.GONE
                dismiss()
            }

            is OnlineOperationState.Error -> {
                (binding.imageViewLoading.drawable as? Animatable)?.stop()
                binding.imageViewLoading.visibility = View.GONE
                when (state.errorId) {
                    MealErrorCodes.ERROR_ID_MEAL_ALREADY_EXISTS -> {
                        binding.textInputMealName.error = state.message
                    }

                    MealErrorCodes.ERROR_ID_MEAL_IN_TEMPLATE -> {
                        binding.checkBoxSaveMeal.buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.md_theme_light_error
                            )
                        )
                        binding.checkBoxSaveMeal.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.md_theme_light_error
                            )
                        )
                        binding.imageViewCheckBoxError.visibility = View.VISIBLE
                        binding.textViewCheckBoxError.visibility = View.VISIBLE
                    }

                    else -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}