package es.upm.macroscore.ui.home.feed.adapter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.upm.macroscore.R
import es.upm.macroscore.ui.model.FoodUIModel

class ItemMealAdapter(
    private val mealPosition: Int,
    private val onEditFood: (mealPosition: Int, foodPosition: Int) -> Unit,
    private val onDeleteFood: (mealPosition: Int, foodId: String) -> Unit,
    private val toggleFavorite: (mealPosition: Int, foodPosition: Int) -> Unit
) :
    ListAdapter<FoodUIModel, ItemMealViewHolder>(ItemMealDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemMealViewHolder {
        return ItemMealViewHolder(
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view_meal, parent, false),
            mealPosition = mealPosition,
            onEditFood = onEditFood,
            onDeleteFood = onDeleteFood,
            toggleFavorite = toggleFavorite
        )
    }

    override fun onBindViewHolder(holder: ItemMealViewHolder, position: Int) {
        holder.bind(
            foodUIModel = getItem(position),
            isLastItem = position == itemCount - 1
        )
    }
}