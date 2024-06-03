package es.upm.macroscore.ui.home.feed.food.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.upm.macroscore.R
import es.upm.macroscore.domain.model.FoodModel

class FoodAdapter(
    private val list: List<FoodModel> = emptyList(),
    private val onItemSelected: (FoodModel) -> Unit
): RecyclerView.Adapter<FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        return FoodViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view_meal, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(list[position], onItemSelected)
    }
}