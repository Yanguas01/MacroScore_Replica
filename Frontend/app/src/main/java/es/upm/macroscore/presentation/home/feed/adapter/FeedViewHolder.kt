package es.upm.macroscore.presentation.home.feed.adapter

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.upm.macroscore.R
import es.upm.macroscore.databinding.ItemRecyclerViewFeedBinding
import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.presentation.home.feed.MealState
import es.upm.macroscore.presentation.home.feed.adapter.adapter.ItemMealAdapter

class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemRecyclerViewFeedBinding.bind(view)
    private lateinit var state: MealState

    fun bind(mealModel: MealModel, touchHelper: ItemTouchHelper, addFood: (String) -> Unit) {
        val context = binding.root.context

        binding.mealTitle.text = mealModel.name
        binding.totalKcal.text =
            context.getString(R.string.total_kcal, mealModel.foods.sumOf { it.kcalPer100 })
        binding.totalCarbs.text =
            context.getString(R.string.total_carbs, mealModel.foods.sumOf { it.carbsPer100 })
        binding.totalProts.text =
            context.getString(R.string.total_prots, mealModel.foods.sumOf { it.protsPer100 })
        binding.totalFats.text =
            context.getString(R.string.total_fats, mealModel.foods.sumOf { it.fatsPer100 })

        binding.ivReorder.setOnLongClickListener {
            touchHelper.startDrag(this)
            true
        }

        state = if (mealModel.foods.isEmpty()) MealState.Empty else MealState.Expanded
        setBindings()

        val itemAdapter = ItemMealAdapter(mealModel.foods)
        binding.recyclerViewMeal.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }

        binding.mealSummarize.setOnClickListener {
            state = if (state == MealState.Expanded) MealState.Collapsed else MealState.Expanded
            setBindings()
        }

        binding.addFood.setOnClickListener {
            addFood(mealModel.name)
        }
    }

    private fun setBindings() {
        when (state) {
            MealState.Collapsed -> {
                binding.mealSummarize.visibility = VISIBLE
                binding.divider.visibility = VISIBLE
                binding.recyclerViewMeal.visibility = GONE
                binding.divider2.visibility = GONE
            }

            MealState.Empty -> {
                binding.mealSummarize.visibility = GONE
                binding.divider.visibility = GONE
                binding.recyclerViewMeal.visibility = GONE
                binding.divider2.visibility = GONE
            }

            MealState.Expanded -> {
                binding.mealSummarize.visibility = VISIBLE
                binding.divider.visibility = VISIBLE
                binding.recyclerViewMeal.visibility = VISIBLE
                binding.divider2.visibility = VISIBLE
            }
        }
    }
}