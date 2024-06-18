package es.upm.macroscore.ui.home.feed.food.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.View.GONE
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import es.upm.macroscore.R
import es.upm.macroscore.databinding.ItemRecyclerViewMealBinding
import es.upm.macroscore.ui.model.FoodUIModel
import kotlin.properties.Delegates

class FoodViewHolder(
    view: View, private val toggleFavorite: (foodPosition: Int) -> Unit
) : RecyclerView.ViewHolder(view) {

    private val binding = ItemRecyclerViewMealBinding.bind(view)
    private var favorite = false

    fun bind(foodUIModel: FoodUIModel?, onItemSelected: (foodId: String) -> Unit) {
        val context = itemView.rootView.context

        if (foodUIModel != null) {
            favorite = foodUIModel.favorite
        }

        binding.buttonMore.visibility = GONE
        binding.cardViewFoodMacros.visibility = GONE
        binding.root.setOnClickListener {
            if (foodUIModel != null) {
                onItemSelected(foodUIModel.id)
            }
        }

        binding.foodName.isSelected = true
        if (foodUIModel != null) {
            binding.foodName.text = foodUIModel.name

            toggleFavoriteUI(context)

            binding.buttonFavourite.setOnClickListener {
                toggleFavorite(bindingAdapterPosition)
                favorite = !favorite
                toggleFavoriteUI(context)
            }
        }
    }

    private fun toggleFavoriteUI(context: Context) {
        binding.buttonFavourite.icon = AppCompatResources.getDrawable(
            context,
            if (favorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite
        )
        binding.buttonFavourite.iconTint = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (favorite) R.color.favorite_food_button_color else R.color.text
            )
        )
    }
}
