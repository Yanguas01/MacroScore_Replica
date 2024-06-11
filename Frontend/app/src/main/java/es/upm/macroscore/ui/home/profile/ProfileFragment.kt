package es.upm.macroscore.ui.home.profile

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentProfileBinding
import es.upm.macroscore.ui.DropdownFieldsBottomSheet
import es.upm.macroscore.ui.EditBottomSheet
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val viewModel by viewModels<ProfileViewModel>()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var bottomSheet: EditBottomSheet? = null
    private var dropdownFieldsBottomSheet: DropdownFieldsBottomSheet? = null

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
        initUIState()
    }

    private fun initUI() {
        initEditButtons()
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.fieldError.collect { error ->
                        bottomSheet?.getTextField()?.error = error
                    }
                }
                launch {
                    viewModel.stopAnimationEvent.collect {
                        bottomSheet?.stopEndIconAnimation()
                    }
                }
                launch {
                    viewModel.closeDialogEvent.collect {
                        bottomSheet?.dismiss()
                        dropdownFieldsBottomSheet?.dismiss()
                    }
                }
            }
        }
    }

    private fun initEditButtons() {
        binding.buttonGeneralData.setOnClickListener { toggleSubButtons(binding.layoutButtonGeneralDataContainer, binding.buttonGeneralData) }
        binding.buttonPersonalData.setOnClickListener { toggleSubButtons(binding.layoutButtonPersonalDataContainer, binding.buttonPersonalData) }
        binding.buttonUsername.setOnClickListener { initBottomSheet(UserField.USERNAME) }
        binding.buttonEmail.setOnClickListener { initBottomSheet(UserField.EMAIL) }
        binding.buttonGender.setOnClickListener { initBottomSheet(UserField.GENDER) }
        binding.buttonPhysicalActivityLevel.setOnClickListener { initBottomSheet(UserField.PHYSICAL_ACTIVITY_LEVEL) }
        binding.buttonHeight.setOnClickListener { initBottomSheet(UserField.HEIGHT) }
        binding.buttonWeight.setOnClickListener {initBottomSheet(UserField.WEIGHT) }
        binding.buttonAge.setOnClickListener {initBottomSheet(UserField.AGE) }
    }

    private fun toggleSubButtons(container: LinearLayoutCompat, button: MaterialButton) {
        if (container.visibility == View.VISIBLE) {
            rotateIcon(button, R.drawable.ic_animated_next_levorotatory_direction)
            container.animate().alpha(0f).setDuration(300).withEndAction {
                container.visibility = View.GONE
            }.start()
        } else {
            container.visibility = View.VISIBLE
            container.alpha = 0f
            container.animate().alpha(1f).setDuration(300).start()
            rotateIcon(button, R.drawable.ic_animated_next_dextrorotatory_direction)
        }
    }

    private fun rotateIcon(button: MaterialButton, drawableRes: Int) {
        button.setIconResource(drawableRes)
        (button.icon as? AnimatedVectorDrawable)?.start()
    }

    private fun initBottomSheet(userField: UserField) {
        val title : String
        val text : String
        val hint : Int
        val inputType : Int
        var suffix = ""
        val onTextChangedAction : (text : String, bottomSheet : EditBottomSheet) -> Unit

        when (userField) {
            UserField.USERNAME -> {
                title = "Editar nombre de usuario"
                text = viewModel.user.value?.username ?: ""
                hint = R.string.username
                inputType = InputType.TYPE_CLASS_TEXT
                onTextChangedAction = { textChanged, bottomSheet ->
                    if (textChanged != viewModel.user.value?.username) {
                        bottomSheet.startEndIconAnimation()
                        viewModel.checkUsername(textChanged)
                    }
                }
            }
            UserField.EMAIL -> {
                title = "Editar dirección de correo electrónico"
                text = viewModel.user.value?.email ?: ""
                hint = R.string.email
                inputType = InputType.TYPE_CLASS_TEXT
                onTextChangedAction = { textChanged, bottomSheet ->
                    if (textChanged != viewModel.user.value?.email) {
                        bottomSheet.startEndIconAnimation()
                        viewModel.checkEmail(textChanged)
                    }
                }
            }
            UserField.HEIGHT -> {
                title = "Editar altura"
                text = viewModel.user.value?.profile?.height.toString()
                hint = R.string.height
                inputType = InputType.TYPE_CLASS_NUMBER
                suffix = requireContext().getString(R.string.centimeters)
                onTextChangedAction = { textChanged, bottomSheet ->
                    if (textChanged.length >= 4 && textChanged.toInt() !in 50..300) bottomSheet.setTextFieldError("Introduzca una altura válida")
                }
            }
            UserField.WEIGHT -> {
                title = "Editar peso"
                text = viewModel.user.value?.profile?.weight.toString()
                hint = R.string.weight
                inputType = InputType.TYPE_CLASS_NUMBER
                suffix = requireContext().getString(R.string.kilograms)
                onTextChangedAction = { textChanged, bottomSheet ->
                    if (textChanged.length >= 4 && textChanged.toInt() !in 20..400)  bottomSheet.setTextFieldError("Introduzca un peso válido")
                }
            }
            UserField.AGE -> {
                title = "Editar edad"
                text = viewModel.user.value?.profile?.age.toString()
                hint = R.string.age
                inputType = InputType.TYPE_CLASS_NUMBER
                suffix = requireContext().getString(R.string.years)
                onTextChangedAction = { textChanged, bottomSheet ->
                    if (textChanged.length >= 4 && textChanged.toInt() !in 0..150) bottomSheet.setTextFieldError("Introduzca una edad válida")
                }
            }
            UserField.GENDER , UserField.PHYSICAL_ACTIVITY_LEVEL -> {
                initDropdownBottomSheet(userField)
                return
            }
        }

        bottomSheet = EditBottomSheet.Builder(parentFragmentManager)
            .setTitle(title)
            .setText(text)
            .setHint(hint)
            .setInputType(inputType)
            .setSuffix(suffix)
            .setEndIcon(R.drawable.ic_animated_loading)
            .setLoadingButtonIcon(R.drawable.ic_animated_loading)
            .setOnTextChangedAction(onTextChangedAction)
            .setOnAcceptAction { sheet ->
                sheet.block(true)
                sheet.startButtonIconAnimation()
                viewModel.editProfile(userField, sheet.getTextField().editText?.text.toString())
            }
            .setOnCancelAction { it.dismiss() }
            .show()
    }

    private fun initDropdownBottomSheet(userField: UserField) {
        var stringArray : Array<String> = emptyArray()

        var title = ""
        var text = ""
        var hint : Int = -1
        var simpleItems : Int = -1
        var onTextChangedAction : (text : String, bottomSheet : DropdownFieldsBottomSheet) -> Unit = { _, _ -> }

        when (userField) {
            UserField.GENDER -> {
                stringArray = resources.getStringArray(R.array.gender)
                title = "Editar género"
                text = stringArray[viewModel.user.value!!.profile.gender]
                hint = R.string.gender
                simpleItems = R.array.gender
                onTextChangedAction = { textChanged, bottomSheet ->
                    if (textChanged !in stringArray) bottomSheet.setTextFieldError("Introduzca un género válido")
                }
            }
            UserField.PHYSICAL_ACTIVITY_LEVEL -> {
                stringArray = resources.getStringArray(R.array.physical_activity_level)
                title = "Editar nivel de actividad física"
                text = stringArray[viewModel.user.value!!.profile.physicalActivityLevel]
                hint = R.string.physical_activity_level
                simpleItems = R.array.physical_activity_level
                onTextChangedAction = { textChanged, bottomSheet ->
                    if (textChanged !in stringArray) bottomSheet.setTextFieldError(
                        "Introduzca un nivel de actividad física válido"
                    )
                }
            }
            else -> { Log.e("ProfileFragment", "Invalid User Field") }
        }

        dropdownFieldsBottomSheet = DropdownFieldsBottomSheet.Builder(parentFragmentManager)
            .setTitle(title)
            .setText(text)
            .setSimpleItems(simpleItems)
            .setInputType(InputType.TYPE_CLASS_TEXT)
            .setHint(hint)
            .setLoadingButtonIcon(R.drawable.ic_animated_loading)
            .setOnTextChangedAction(onTextChangedAction)
            .setOnAcceptAction { sheet ->
                sheet.block(true)
                sheet.startButtonIconAnimation()
                viewModel.editProfile(
                    userField,
                    stringArray.indexOf(sheet.getTextField().editText?.text.toString()).toString()
                )
            }
            .setOnCancelAction { sheet ->
                sheet.dismiss()
            }.show()
    }
}