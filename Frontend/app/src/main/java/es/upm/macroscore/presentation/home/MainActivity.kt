package es.upm.macroscore.presentation.home

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
    }

    private fun initUi() {
        initNavigation()
    }

    private fun initNavigation() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            updateIndicatorPosition(item.itemId)
            item.onNavDestinationSelected(navController)
            true
        }

        binding.bottomNavigationView.post {
            updateIndicatorPosition(binding.bottomNavigationView.menu.getItem(1).itemId)
        }
    }

    private fun updateIndicatorPosition(itemId: Int) {
        val menuView = binding.bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        val menuItemIndex = getMenuItemIndex(itemId)
        val menuItemView = menuView.getChildAt(menuItemIndex) as View

        val targetWidth = menuItemView.width
        val targetStartX = menuItemView.left

        val startXAnimation =
            ValueAnimator.ofFloat(binding.activeItemIndicator.x, targetStartX.toFloat())
        startXAnimation.addUpdateListener { animation ->
            binding.activeItemIndicator.x = animation.animatedValue as Float
        }

        val widthAnimation = ValueAnimator.ofInt(binding.activeItemIndicator.width, targetWidth)
        widthAnimation.addUpdateListener { animation ->
            val layoutParams = binding.activeItemIndicator.layoutParams
            layoutParams.width = animation.animatedValue as Int
            binding.activeItemIndicator.layoutParams = layoutParams
        }

        val animatorSet = AnimatorSet()
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.playTogether(startXAnimation, widthAnimation)
        animatorSet.duration = 250
        animatorSet.start()
    }

    private fun getMenuItemIndex(itemId: Int): Int {
        for (i in 0 until binding.bottomNavigationView.menu.size()) {
            if (binding.bottomNavigationView.menu.getItem(i).itemId == itemId) {
                return i
            }
        }
        return -1
    }
}
