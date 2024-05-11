package es.upm.macroscore.presentation.home.feed

import android.graphics.Canvas
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentFeedBinding
import es.upm.macroscore.presentation.EditBottomSheet
import es.upm.macroscore.presentation.home.feed.adapter.FeedAdapter
import es.upm.macroscore.presentation.model.MealUIModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val viewModel by activityViewModels<FeedViewModel>()

    private lateinit var feedAdapter: FeedAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    private var mealList: List<MealUIModel> = emptyList()

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initUIState()
        initItemTouchHelper()
        initRecyclerView()
        binding.addMeal.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_mealDialogFragment)
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
                    val adapter = recyclerView.adapter as FeedAdapter
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    adapter.moveItem(from, to)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

                override fun isLongPressDragEnabled(): Boolean = false

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.let { itemView ->
                            itemView.animate()
                                .scaleX(1.05f)
                                .scaleY(1.05f)
                                .translationZ(10f)
                                .duration = 150
                        }
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .translationZ(0f)
                        .duration = 150
                }

                override fun onChildDraw(
                    c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                    dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
                ) {
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        val itemView = viewHolder.itemView
                        itemView.translationX = dX
                        itemView.translationY = dY
                    } else {
                        super.onChildDraw(
                            c,
                            recyclerView,
                            viewHolder,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive
                        )
                    }
                }
            }

        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewFeed)
    }

    private fun initRecyclerView() {
        feedAdapter = FeedAdapter(
            touchHelper = itemTouchHelper,
            onEditMeal = {
                onEditMeal(it)
            },
            onDeleteMeal = {
                onDeleteMeal(it)
            },
            addFood = {
                findNavController().navigate(
                    FeedFragmentDirections.actionFeedFragmentToFoodDialogFragment(it)
                )
            })
        binding.recyclerViewFeed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = feedAdapter
        }
    }

    private fun onEditMeal(position: Int) {
        val meal = mealList[position]

        val bottomSheet = EditBottomSheet.Builder()
            .setTitle("Cambiar Nombre")
            .setHint(R.string.meal_name)
            .setInputType(InputType.TYPE_CLASS_TEXT)
            .setEndIcon(R.drawable.ic_animated_loading)
            .setText(meal.name)
            .setOnAcceptAction {
                simularLlamadaBackend {
                    Toast.makeText(requireContext(), "Aceptar", Toast.LENGTH_SHORT).show()
                }
            }
            .setOnCancelAction {
                Toast.makeText(requireContext(), "Cancelar", Toast.LENGTH_SHORT).show()
            }.build()

        bottomSheet.onAcceptWithResponse = { onResponseComplete ->
            bottomSheet.startEndIconAnimation()

            simularLlamadaBackend {
                bottomSheet.stopEndIconAnimation()
                onResponseComplete()
            }
        }

        bottomSheet.show(requireActivity().supportFragmentManager, "bottom_sheet")
    }

    private fun simularLlamadaBackend(onComplete: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            onComplete()
        }, 2000)
    }

    private fun onDeleteMeal(position: Int) {

    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mealList.collect {
                    mealList = it
                    feedAdapter.updateList(it)
                    updateHint()
                }
            }
        }
    }

    private fun updateHint() {
        binding.textViewHint.visibility =
            if (binding.recyclerViewFeed.adapter?.itemCount != 0) View.GONE else View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}