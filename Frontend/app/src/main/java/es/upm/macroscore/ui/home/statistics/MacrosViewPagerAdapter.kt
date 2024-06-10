package es.upm.macroscore.ui.home.statistics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import es.upm.macroscore.R
import es.upm.macroscore.domain.model.DailyIntakeModel
import es.upm.macroscore.domain.model.NutritionalNeedsModel

class MacrosViewPagerAdapter(
    private val dailyIntakeList: List<Pair<DailyIntakeModel, NutritionalNeedsModel>>
) : RecyclerView.Adapter<MacrosViewPagerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MacrosViewPagerViewHolder {
        return MacrosViewPagerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view_pager_macros, parent, false))
    }

    override fun getItemCount(): Int = dailyIntakeList.size

    override fun onBindViewHolder(holder: MacrosViewPagerViewHolder, position: Int) {
        val dailyIntake = dailyIntakeList[position]
        holder.bind(dailyIntake.first, dailyIntake.second)
    }
}
