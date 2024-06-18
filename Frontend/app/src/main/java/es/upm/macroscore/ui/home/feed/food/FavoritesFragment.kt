package es.upm.macroscore.ui.home.feed.food

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.core.extensions.onTextChanged
import es.upm.macroscore.databinding.FragmentFavoritesBinding
import es.upm.macroscore.ui.EditBottomSheet
import es.upm.macroscore.ui.home.feed.FeedViewModel
import es.upm.macroscore.ui.home.feed.food.adapter.FavoriteAdapter
import es.upm.macroscore.ui.model.FoodUIModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private val viewModel by activityViewModels<FeedViewModel>()

    private lateinit var favoriteAdapter: FavoriteAdapter

    private var bottomSheet: EditBottomSheet? = null

    private var _binding: FragmentFavoritesBinding? = null
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

    private fun toggleFavorite(foodPosition: Int) {
        val foodUIModel = favoriteAdapter.currentList[foodPosition]
        viewModel.toggleFavorite(foodUIModel)
    }

    private fun initRecyclerView() {
        favoriteAdapter = FavoriteAdapter(
            onItemSelected = { foodId -> onAddFoodAction(foodId) },
            toggleFavorite = { foodPosition -> toggleFavorite(foodPosition) }
        )
        binding.layoutSearchFood.recyclerViewFood.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoriteAdapter
        }
    }

    private fun onAddFoodAction(foodId: String) {
        bottomSheet = EditBottomSheet.Builder(parentFragmentManager)
            .setTitle("Introducir cantidad")
            .setHint(R.string.quantity)
            .setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            .setSuffix("gramos")
            .setLoadingButtonIcon(R.drawable.ic_animated_loading)
            .setOnTextChangedAction { text, bottomSheet ->
                if (text.isNotBlank()) {
                    if (text.toDouble() <= 0.0) bottomSheet.setTextFieldError("Introduzca una cantidad válida")
                }
            }
            .setOnAcceptAction { bottomSheetObject ->
                bottomSheetObject.startButtonIconAnimation()
                bottomSheetObject.block(true)
                viewModel.addFoodToMeal(foodId, bottomSheetObject.getText().toDouble())
            }
            .setOnCancelAction { bottomSheetObject ->
                bottomSheetObject.dismiss()
            }.show()
    }

    private fun initSearchField() {
        binding.layoutSearchFood.editTextSearch.onTextChanged { text ->
            viewModel.setPatternFavorites(text)
        }
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.favoritesList.collectLatest { list ->
                        favoriteAdapter.submitList(list)
                    }
                }
                launch {
                    viewModel.closeFoodBottomSheetEvent.collect {
                        bottomSheet?.stopButtonIconAnimation()
                        bottomSheet?.dismiss()
                        (parentFragment as DialogFragment).dismiss()
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }
}