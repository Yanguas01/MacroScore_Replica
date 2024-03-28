package es.upm.macroscore.presentation.home.statistics

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter

class MacrosViewPagerAdapter(statisticsFragment: StatisticsFragment) : FragmentStateAdapter(statisticsFragment) {

    override fun getItemCount(): Int = 7

    override fun createFragment(position: Int): Fragment {
        return MacrosChartsFragment.newInstance(position)
    }
}
