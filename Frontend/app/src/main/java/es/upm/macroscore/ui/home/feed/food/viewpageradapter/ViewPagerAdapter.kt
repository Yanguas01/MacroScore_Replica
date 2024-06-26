package es.upm.macroscore.ui.home.feed.food.viewpageradapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import es.upm.macroscore.ui.home.feed.food.FavoritesFragment
import es.upm.macroscore.ui.home.feed.food.SearchFragment

class ViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private val fragments = arrayOf(SearchFragment(), FavoritesFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}