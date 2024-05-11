package es.upm.macroscore.presentation.home.feed.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.upm.macroscore.R
import es.upm.macroscore.databinding.ItemRecyclerViewFeedBinding
import es.upm.macroscore.presentation.home.feed.MealState
import es.upm.macroscore.presentation.home.feed.adapter.adapter.ItemMealAdapter
import es.upm.macroscore.presentation.model.MealUIModel

class FeedViewHolder(
    view: View,
    private val onUpdate: (position: Int) -> Unit,
    private val onEditMeal: (position: Int) -> Unit,
    private val onDeleteMeal: (position: Int) -> Unit
) : RecyclerView.ViewHolder(view) {

    private lateinit var context: Context
    private val binding = ItemRecyclerViewFeedBinding.bind(view)

    fun bind(mealUIModel: MealUIModel, position: Int, touchHelper: ItemTouchHelper, addFood: (String) -> Unit) {
        context = binding.root.context

        binding.mealTitle.text = mealUIModel.name
        binding.totalKcal.text =
            context.getString(R.string.total_kcal, mealUIModel.foods.sumOf { it.kcalPer100 })
        binding.totalCarbs.text =
            context.getString(R.string.total_carbs, mealUIModel.foods.sumOf { it.carbsPer100 })
        binding.totalProts.text =
            context.getString(R.string.total_prots, mealUIModel.foods.sumOf { it.protsPer100 })
        binding.totalFats.text =
            context.getString(R.string.total_fats, mealUIModel.foods.sumOf { it.fatsPer100 })

        binding.ivReorder.setOnLongClickListener {
            touchHelper.startDrag(this)
            true
        }

        if (mealUIModel.state == null) {
            mealUIModel.state =
                if (mealUIModel.foods.isEmpty()) MealState.EMPTY else MealState.EXPANDED
        }

        updateUIBasedOnState(mealUIModel.state)

        val itemAdapter = ItemMealAdapter(mealUIModel.foods)
        binding.recyclerViewMeal.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }

        initStateButtonsListeners(mealUIModel, position)
        initAddFoodButtonListener(mealUIModel.name, addFood)
    }

    private fun updateUIBasedOnState(state: MealState?) {
        when (state) {
            MealState.MINIMIZED -> {
                binding.cardViewMealItem.visibility = GONE
            }

            MealState.COLLAPSED -> {
                binding.cardViewMealItem.visibility = VISIBLE
                binding.mealSummarize.visibility = VISIBLE
                binding.divider.visibility = VISIBLE
                binding.recyclerViewMeal.visibility = GONE
                binding.divider2.visibility = GONE
                binding.buttonAddFood.visibility = VISIBLE
            }

            MealState.EMPTY -> {
                binding.cardViewMealItem.visibility = VISIBLE
                binding.mealSummarize.visibility = GONE
                binding.divider.visibility = GONE
                binding.recyclerViewMeal.visibility = GONE
                binding.divider2.visibility = GONE
                binding.buttonAddFood.visibility = VISIBLE
            }

            MealState.EXPANDED -> {
                binding.cardViewMealItem.visibility = VISIBLE
                binding.mealSummarize.visibility = VISIBLE
                binding.divider.visibility = VISIBLE
                binding.recyclerViewMeal.visibility = VISIBLE
                binding.divider2.visibility = VISIBLE
                binding.buttonAddFood.visibility = VISIBLE
            }

            else -> {
                Log.e("FeedViewHolder", "MEAL STATE NULL")
            }
        }
    }

    private fun initStateButtonsListeners(mealModel: MealUIModel, position: Int) {
        binding.buttonMoreSettings.setOnClickListener {
            showPopupMenu(it, R.menu.meal_menu, position)
        }
        binding.buttonMinimize.setOnClickListener {
            mealModel.state = if (mealModel.state == MealState.MINIMIZED) {
                if (mealModel.foods.isEmpty()) MealState.EMPTY else MealState.COLLAPSED
            } else {
                MealState.MINIMIZED
            }
            updateUIBasedOnState(mealModel.state)
            onUpdate(adapterPosition)
        }

        binding.mealSummarize.setOnClickListener {
            mealModel.state = if (mealModel.state == MealState.COLLAPSED) {
                MealState.EXPANDED
            } else {
                MealState.COLLAPSED
            }
            updateUIBasedOnState(mealModel.state)
            onUpdate(adapterPosition)
        }

    }


    @SuppressLint("RestrictedApi")
    private fun showPopupMenu(v: View, @MenuRes menuRes: Int, position: Int) {
        val popupMenu = PopupMenu(context, v)
        popupMenu.menuInflater.inflate(menuRes, popupMenu.menu)

        if (popupMenu.menu is MenuBuilder) {
            val menuBuilder = popupMenu.menu as MenuBuilder
            menuBuilder.setOptionalIconsVisible(true)
            for (item in menuBuilder.visibleItems) {
                if (item.icon != null) {
                    item.icon = InsetDrawable(item.icon, 16, 0, 16, 0)
                }
            }
        }

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId) {
                R.id.edit -> {
                    onEditMeal(position)
                    true
                }
                R.id.delete -> {
                    onDeleteMeal(position)
                    true
                }
                else -> { false }
            }
        }

        popupMenu.show()
    }

    private fun initAddFoodButtonListener(mealName: String, addFood: (String) -> Unit) {
        binding.buttonAddFood.setOnClickListener { addFood(mealName) }
    }
}