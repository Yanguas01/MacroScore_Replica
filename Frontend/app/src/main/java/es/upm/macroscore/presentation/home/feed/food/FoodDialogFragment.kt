package es.upm.macroscore.presentation.home.feed.food

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentFoodDialogBinding
import es.upm.macroscore.presentation.home.feed.FeedViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FoodDialogFragment : DialogFragment() {

    private val viewModel by activityViewModels<FeedViewModel>()

    private lateinit var navController: NavController
    private val args: FoodDialogFragmentArgs by navArgs()

    private var _binding: FragmentFoodDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setCurrentMealName(args.mealName)
        initUI()
    }

    private fun initUI() {
        initUIState()
        initNavigation()
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isReadyToDismiss.collect {
                    this@FoodDialogFragment.dismiss()
                }
            }
        }
    }

    private fun initNavigation() {
        val navHost =
            childFragmentManager.findFragmentById(R.id.fragment_container_view_food) as NavHostFragment
        navController = navHost.navController

        binding.navigationView.setupWithNavController(navController)

        binding.navigationView.setOnItemSelectedListener { item ->
            updateIndicatorPosition(item.itemId)
            item.onNavDestinationSelected(navController)
            true
        }

        binding.navigationView.post {
            updateIndicatorPosition(binding.navigationView.menu.getItem(0).itemId)
        }
    }

    private fun updateIndicatorPosition(itemId: Int) {
        val menuView = binding.navigationView.getChildAt(0) as BottomNavigationMenuView
        val menuItemIndex = getMenuItemIndex(itemId)
        val menuItemView = menuView.getChildAt(menuItemIndex) as View

        val targetWidth = menuItemView.width
        val targetStartX = menuItemView.left + (binding.navigationView.width / 2 - targetWidth)

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
        for (i in 0 until binding.navigationView.menu.size()) {
            if (binding.navigationView.menu.getItem(i).itemId == itemId) {
                return i
            }
        }
        return -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}
