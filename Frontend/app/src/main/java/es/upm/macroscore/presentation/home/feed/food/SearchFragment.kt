package es.upm.macroscore.presentation.home.feed.food

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentSearchBinding
import es.upm.macroscore.databinding.LayoutWeightDialogBinding
import es.upm.macroscore.presentation.home.feed.FeedViewModel
import es.upm.macroscore.presentation.home.feed.adapter.FeedAdapter
import es.upm.macroscore.presentation.home.feed.food.adapter.FoodAdapter
import es.upm.macroscore.presentation.home.feed.food.adapter.SearchViewModel

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel by activityViewModels<FeedViewModel>()
    private lateinit var foodAdapter: FoodAdapter

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        foodAdapter = FoodAdapter(viewModel.getFood()) { foodModel ->
            val bottomSheet = SetWeightBottomSheet {
                viewModel.addFood(foodModel)
                viewModel.closeDialog()
            }
            bottomSheet.show(requireActivity().supportFragmentManager, "bottom_sheet")
        }
        binding.layoutSearchFood.recyclerViewFood.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodAdapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}