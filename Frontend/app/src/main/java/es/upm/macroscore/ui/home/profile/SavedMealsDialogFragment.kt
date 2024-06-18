package es.upm.macroscore.ui.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentSavedMealsDialogBinding
import es.upm.macroscore.ui.home.profile.adapter.SavedMealsAdapter
import kotlinx.coroutines.launch


class SavedMealsDialogFragment : DialogFragment() {

    private val viewModel by activityViewModels<ProfileViewModel>()

    private var savedMealsAdapter: SavedMealsAdapter? = null

    private var _binding: FragmentSavedMealsDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedMealsDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initUIState()
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.user.collect { user ->
                        if (user != null) {
                            savedMealsAdapter?.submitList(user.orderMeal)
                        }
                    }
                }
            }
        }
    }

    private fun initUI() {
        initToolbar()
        initRecyclerView()
    }

    private fun initToolbar() {
        binding.buttonClose.setOnClickListener { this.dismiss() }
    }

    private fun initRecyclerView() {
        savedMealsAdapter = SavedMealsAdapter(onDelete = { onDeleteMeal(it) })
        savedMealsAdapter?.submitList(viewModel.user.value?.orderMeal) {
            binding.recyclerViewSavedMeals.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = savedMealsAdapter
            }
        }
    }

    private fun onDeleteMeal(mealName: String) {
        MaterialAlertDialogBuilder(requireContext()).setTitle("¿Estás seguro de que quieres eliminar la comida?")
            .setIcon(ResourcesCompat.getDrawable(resources, R.drawable.ic_alert, null))
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }.setPositiveButton("Aceptar") { dialog, _ ->
                viewModel.deleteMealFromSavedMeals(mealName)
                dialog.dismiss()
            }.show()
    }
}