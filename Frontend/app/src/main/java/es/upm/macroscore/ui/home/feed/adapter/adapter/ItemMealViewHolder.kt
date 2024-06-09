package es.upm.macroscore.ui.home.feed.adapter.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.view.View.GONE
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import es.upm.macroscore.R
import es.upm.macroscore.databinding.ItemRecyclerViewMealBinding
import es.upm.macroscore.ui.home.feed.FoodState
import es.upm.macroscore.ui.model.FoodUIModel

class ItemMealViewHolder(
    view: View,
    private val mealPosition: Int,
    private val onEditFood: (mealPosition: Int, foodPosition: Int) -> Unit,
    private val onDeleteFood: (mealPosition: Int, foodId: String) -> Unit
): RecyclerView.ViewHolder(view) {

    private lateinit var context: Context
    private val binding = ItemRecyclerViewMealBinding.bind(view)

    fun bind(foodUIModel: FoodUIModel, isLastItem: Boolean) {
        context = binding.root.context

        binding.foodName.isSelected = true
        binding.foodName.text = foodUIModel.name
        binding.foodKcal.text = context.getString(R.string.kcal_per_100, foodUIModel.kcalPer100 * foodUIModel.weight!! / 100)
        binding.foodCarbs.text = context.getString(R.string.carbs_per_100, foodUIModel.carbsPer100 * foodUIModel.weight / 100)
        binding.foodProts.text = context.getString(R.string.prots_per_100, foodUIModel.protsPer100 * foodUIModel.weight / 100)
        binding.foodFats.text = context.getString(R.string.fats_per_100, foodUIModel.fatsPer100 * foodUIModel.weight / 100)

        updateUIBasedOnState(foodUIModel.state)

        itemView.setOnClickListener {
            foodUIModel.state = if (foodUIModel.state == FoodState.COLLAPSED) FoodState.EXPANDED else FoodState.COLLAPSED
            updateUIBasedOnState(foodUIModel.state)
        }

        if (isLastItem) binding.divider.visibility = GONE

        initButtonsListeners(foodUIModel)
    }

    private fun updateUIBasedOnState(state: FoodState) {
        when (state) {
            FoodState.COLLAPSED -> {
                binding.cardViewFoodMacros.visibility = GONE
            }
            FoodState.EXPANDED -> {
                binding.cardViewFoodMacros.visibility = VISIBLE
            }
        }
    }

    private fun initButtonsListeners(foodModel: FoodUIModel) {
        binding.buttonMore.setOnClickListener {
            showPopupMenu(it, foodModel, R.menu.meal_menu)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showPopupMenu(v: View, foodModel: FoodUIModel, @MenuRes menuRes: Int) {
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
                    onEditFood(mealPosition, bindingAdapterPosition)
                    true
                }
                R.id.delete -> {
                    onDeleteFood(mealPosition, foodModel.id)
                    true
                }
                else -> { false }
            }
        }

        popupMenu.show()
    }
}