package es.upm.macroscore.ui.home.feed.food

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.core.extensions.onTextChanged
import es.upm.macroscore.databinding.FragmentSearchBinding
import es.upm.macroscore.ui.EditBottomSheet
import es.upm.macroscore.ui.EditBottomSheetInput
import es.upm.macroscore.ui.home.feed.FeedViewModel
import es.upm.macroscore.ui.home.feed.food.adapter.FoodAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel by activityViewModels<FeedViewModel>()

    private lateinit var foodAdapter: FoodAdapter

    private var bottomSheet: EditBottomSheet? = null

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initUIState()
    }

    private fun initUI() {
        initRecyclerView()
        initSearchField()
    }

    private fun initRecyclerView() {
        foodAdapter = FoodAdapter { foodId ->
            onAddFoodAction(foodId)
        }
        binding.layoutSearchFood.recyclerViewFood.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodAdapter
        }
    }

    private fun onAddFoodAction(foodId: String) {
        bottomSheet = EditBottomSheet.Builder(parentFragmentManager)
            .setTitle("Introducir cantidad")
            .setHint(R.string.quantity)
            .setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            .setSuffix("gramos")
            .setLoadingButtonIcon(R.drawable.ic_animated_loading)
            .setEnableButtonCondition { input ->
                val double = input as EditBottomSheetInput.DoubleInput
                double.value > 0
            }
            .setOnAcceptAction { bottomSheetObject ->
                bottomSheetObject.startEndIconAnimation()
                bottomSheetObject.block(true)
                viewModel.addFoodToMeal(foodId, bottomSheetObject.getText().toDouble())
            }
            .setOnCancelAction { bottomSheetObject ->
                bottomSheetObject.dismiss()
            }.show()
    }

    private fun initSearchField() {
        binding.layoutSearchFood.editTextSearch.onTextChanged { text ->
            viewModel.setPattern(text)
        }
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.foods.collectLatest { pagingData ->
                        foodAdapter.submitData(pagingData)
                    }
                }
                launch {
                    viewModel.closeBottomSheetEvent.collect {
                        bottomSheet?.stopButtonIconAnimation()
                        bottomSheet?.dismiss()
                        (parentFragment as DialogFragment).dismiss()
                    }
                }
            }
        }
        foodAdapter.addLoadStateListener { loadState ->
            handleLoadState(loadState)
        }
    }

    private fun handleLoadState(loadState: CombinedLoadStates) {
        when (val refreshState = loadState.refresh) {
            is LoadState.Loading -> {
                if (foodAdapter.itemCount == 0) {
                    binding.layoutSearchFood.layoutLoadingSearchFood.visibility = View.VISIBLE
                    binding.layoutSearchFood.layoutLoadingSearchFood.startShimmer()
                }
            }

            is LoadState.Error -> {
                lifecycleScope.launch {
                    foodAdapter.submitData(PagingData.empty())
                }
                binding.layoutSearchFood.layoutLoadingSearchFood.stopShimmer()
                binding.layoutSearchFood.layoutLoadingSearchFood.visibility = View.GONE
                Toast.makeText(requireContext(), refreshState.error.message, Toast.LENGTH_SHORT)
                    .show()
            }

            is LoadState.NotLoading -> {
                binding.layoutSearchFood.layoutLoadingSearchFood.stopShimmer()
                binding.layoutSearchFood.layoutLoadingSearchFood.visibility = View.GONE
            }
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