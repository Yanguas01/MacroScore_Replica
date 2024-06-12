package es.upm.macroscore.ui.home.feed

import android.graphics.Canvas
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentFeedBinding
import es.upm.macroscore.ui.EditBottomSheet
import es.upm.macroscore.ui.home.feed.adapter.FeedAdapter
import es.upm.macroscore.ui.states.OnlineOperationState
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val viewModel by activityViewModels<FeedViewModel>()

    private lateinit var feedAdapter: FeedAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private var originalItemAnimator: RecyclerView.ItemAnimator? = null

    private var state: OnlineOperationState = OnlineOperationState.Idle

    private var bottomSheet: EditBottomSheet? = null

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initUIState()
    }

    private fun initUI() {
        initToolbar()
        initItemTouchHelper()
        initRecyclerView()
        binding.addMeal.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_mealDialogFragment)
        }
    }

    private fun initToolbar() {
        binding.buttonPreviousDay.setOnClickListener {
            binding.recyclerViewFeed.itemAnimator = null
            viewModel.previousDay()
        }
        binding.buttonNextDay.setOnClickListener {
            binding.recyclerViewFeed.itemAnimator = null
            viewModel.nextDay()
        }
    }

    private fun initItemTouchHelper() {
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    binding.recyclerViewFeed.itemAnimator = originalItemAnimator
                    val from = viewHolder.bindingAdapterPosition
                    val to = target.bindingAdapterPosition
                    viewModel.moveItem(from, to)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

                override fun isLongPressDragEnabled(): Boolean = false

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?, actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.let { itemView ->
                            itemView.animate().scaleX(1.05f).scaleY(1.05f)
                                .translationZ(10f).duration = 150
                        }
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.animate().scaleX(1.0f).scaleY(1.0f)
                        .translationZ(0f).duration = 150
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        val itemView = viewHolder.itemView
                        itemView.translationX = dX
                        itemView.translationY = dY
                    } else {
                        super.onChildDraw(
                            c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                        )
                    }
                }
            }

        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewFeed)
    }

    private fun initRecyclerView() {
        feedAdapter = FeedAdapter(
            viewModel = viewModel,
            touchHelper = itemTouchHelper,
            onUpdate = {
                binding.recyclerViewFeed.itemAnimator = originalItemAnimator
                feedAdapter.notifyItemChanged(it)
            },
            onEditMeal = { onEditMeal(it) },
            onDeleteMeal = { onDeleteMeal(it) },
            onEditFood = { mealPosition, foodPosition -> onEditFood(mealPosition, foodPosition) },
            onDeleteFood = { mealPosition, foodId -> onDeleteFood(mealPosition, foodId) },
            addFood = {
            findNavController().navigate(
                FeedFragmentDirections.actionFeedFragmentToFoodDialogFragment(it)
            )
        })
        binding.recyclerViewFeed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = feedAdapter
            originalItemAnimator = itemAnimator
            itemAnimator = null
        }
    }

    private fun onEditMeal(position: Int) {
        val meal = feedAdapter.currentList[position]

        bottomSheet = EditBottomSheet.Builder(requireActivity().supportFragmentManager)
            .setTitle("Cambiar Nombre").setHint(R.string.meal_name)
            .setInputType(InputType.TYPE_CLASS_TEXT).setText(meal.name)
            .setLoadingButtonIcon(R.drawable.ic_animated_loading)
            .setOnTextChangedAction { text, bottomSheet ->
                if (text.isBlank()) bottomSheet.setTextFieldError("El campo no puede estar vacío")
            }
            .setOnAcceptAction { bottomSheetObject ->
                binding.recyclerViewFeed.itemAnimator = originalItemAnimator
                bottomSheetObject.startButtonIconAnimation()
                bottomSheetObject.block(true)
                viewModel.renameMeal(meal.id, bottomSheetObject.getText())
            }.setOnCancelAction { bottomSheetObject ->
                bottomSheetObject.dismiss()
            }.show()
    }

    private fun onDeleteMeal(mealId: String) {

        MaterialAlertDialogBuilder(requireContext()).setTitle("¿Estás seguro de que quieres eliminar la comida?")
            .setIcon(ResourcesCompat.getDrawable(resources, R.drawable.ic_alert, null))
            .setMessage("Si lo eliminas, tendrás que volver a crearlo.")
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }.setPositiveButton("Aceptar") { dialog, _ ->
                binding.recyclerViewFeed.itemAnimator = originalItemAnimator
                viewModel.deleteMeal(mealId = mealId)
                dialog.dismiss()
            }.show()
    }

    private fun onEditFood(mealPosition: Int, foodPosition: Int) {
        val meal = feedAdapter.currentList[mealPosition]
        val food = meal.items[foodPosition]

        bottomSheet = EditBottomSheet.Builder(parentFragmentManager)
            .setTitle("Introducir cantidad")
            .setText(food.weight.toString())
            .setHint(R.string.quantity)
            .setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            .setSuffix("gramos")
            .setLoadingButtonIcon(R.drawable.ic_animated_loading)
            .setOnTextChangedAction { text, bottomSheet ->
                if (text.toDouble() > 0) bottomSheet.setTextFieldError("La cantidad no puede ser menor o igual que 0")
            }
            .setOnAcceptAction { bottomSheetObject ->
                binding.recyclerViewFeed.itemAnimator = originalItemAnimator
                bottomSheetObject.startButtonIconAnimation()
                bottomSheetObject.block(true)
                viewModel.editFoodWeight(meal.id, food.id, bottomSheetObject.getText().toDouble())
            }
            .setOnCancelAction { bottomSheetObject ->
                bottomSheetObject.dismiss()
            }.show()
    }

    private fun onDeleteFood(mealPosition: Int, foodId: String) {
        val mealId = feedAdapter.currentList[mealPosition].id

        MaterialAlertDialogBuilder(requireContext()).setTitle("¿Estás seguro de que quieres eliminar el alimento?")
            .setIcon(ResourcesCompat.getDrawable(resources, R.drawable.ic_alert, null))
            .setMessage("Si lo eliminas, tendrás que volver a crearlo.")
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }.setPositiveButton("Aceptar") { dialog, _ ->
                binding.recyclerViewFeed.itemAnimator = originalItemAnimator
                viewModel.deleteFood(mealId = mealId, foodId = foodId)
                dialog.dismiss()
            }.show()
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.mealList.collect {
                        feedAdapter.submitList(it) {
                            if (state !is OnlineOperationState.Idle) {
                                updateHint()
                                viewModel.setStateAsSuccess()
                            }
                        }
                    }
                }
                launch {
                    viewModel.currentDate.collect { currentDate ->
                        if (currentDate != null) {
                            binding.textViewDate.text =
                                this@FeedFragment.requireContext().getString(
                                    R.string.current_date,
                                    currentDate.dayOfWeek,
                                    currentDate.dayOfMonth,
                                    currentDate.month
                                )
                        }
                    }
                }
                launch {
                    viewModel.feedActionState.collect { newState ->
                        Log.d("FeedFragment", state.toString())
                        state = newState
                        setState(state)
                    }
                }
                launch {
                    viewModel.closeBottomSheetEvent.collect {
                        bottomSheet?.stopButtonIconAnimation()
                        bottomSheet?.dismiss()
                    }
                }
            }
        }
    }

    private fun setState(state: OnlineOperationState) {
        when (state) {
            is OnlineOperationState.Idle -> {}
            is OnlineOperationState.Loading -> {
                binding.shimmerLayoutFeed.shimmerLayoutFeed.visibility = View.VISIBLE
                binding.shimmerLayoutFeed.shimmerLayoutFeed.startShimmer()
                binding.addMeal.isEnabled = false
                binding.recyclerViewFeed.visibility = View.GONE
                binding.textViewHint.visibility = View.GONE
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            }
            is OnlineOperationState.Success -> {
                binding.shimmerLayoutFeed.shimmerLayoutFeed.stopShimmer()
                binding.shimmerLayoutFeed.shimmerLayoutFeed.visibility = View.GONE
                binding.addMeal.isEnabled = true
                binding.recyclerViewFeed.visibility = View.VISIBLE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
            is OnlineOperationState.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                binding.shimmerLayoutFeed.shimmerLayoutFeed.stopShimmer()
                binding.shimmerLayoutFeed.shimmerLayoutFeed.visibility = View.GONE
                binding.addMeal.isEnabled = true
                binding.recyclerViewFeed.visibility = View.VISIBLE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    private fun updateHint() {
        binding.textViewHint.visibility =
            if (feedAdapter.itemCount != 0 ||
                state is OnlineOperationState.Loading
            ) View.GONE else View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (state !is OnlineOperationState.Loading) {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}
