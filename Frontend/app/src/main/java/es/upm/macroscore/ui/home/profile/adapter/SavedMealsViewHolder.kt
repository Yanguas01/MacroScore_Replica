package es.upm.macroscore.ui.home.profile.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import es.upm.macroscore.databinding.ItemSavedMealsBinding

class SavedMealsViewHolder(
    view: View,
    private val onDelete: (mealName: String) -> Unit
): RecyclerView.ViewHolder(view) {

    private val binding = ItemSavedMealsBinding.bind(view)

    fun bind(item: String, isLastItem: Boolean) {
        binding.textViewMealName.text = item
        binding.buttonDelete.setOnClickListener { onDelete(item) }

        if (isLastItem) binding.divider.visibility = View.GONE
    }
}
