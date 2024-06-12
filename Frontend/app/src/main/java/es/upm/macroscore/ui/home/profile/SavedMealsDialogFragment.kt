package es.upm.macroscore.ui.home.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentSavedMealsDialogBinding
import es.upm.macroscore.ui.home.feed.FeedFragmentDirections
import es.upm.macroscore.ui.home.feed.adapter.FeedAdapter
import es.upm.macroscore.ui.home.profile.adapter.SavedMealsAdapter


class SavedMealsDialogFragment : DialogFragment() {

    private val viewModel by activityViewModels<ProfileViewModel>()

    private var _binding: FragmentSavedMealsDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedMealsDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initToolbar()
        initRecyclerView()
    }

    private fun initToolbar() {
        binding.buttonClose.setOnClickListener { this.dismiss() }
    }

    private fun initRecyclerView() {
        val savedMealsAdapter = SavedMealsAdapter(
            onDelete = { onDeleteMeal(it) }
        )
        savedMealsAdapter.submitList(viewModel.user.value?.orderMeal) {
            binding.recyclerViewSavedMeals.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = savedMealsAdapter
            }
        }
    }

    private fun onDeleteMeal(mealName: String) {
        viewModel.deleteMealFromSavedMeals(mealName)
    }
}