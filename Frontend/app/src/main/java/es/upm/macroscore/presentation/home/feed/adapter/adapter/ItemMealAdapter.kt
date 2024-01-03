package es.upm.macroscore.presentation.home.feed.adapter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.upm.macroscore.R
import es.upm.macroscore.domain.model.FoodModel

class ItemMealAdapter(private val list: List<FoodModel>): RecyclerView.Adapter<ItemMealViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemMealViewHolder {
        return ItemMealViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view_meal, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ItemMealViewHolder, position: Int) {
        holder.bind(list[position])
    }
}