package es.upm.macroscore.ui.home.feed.food

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentFoodDialogBinding
import es.upm.macroscore.ui.home.feed.FeedViewModel
import es.upm.macroscore.ui.home.feed.food.viewpageradapter.ViewPagerAdapter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FoodDialogFragment : DialogFragment() {

    private val viewModel by activityViewModels<FeedViewModel>()

    private val args: FoodDialogFragmentArgs by navArgs()

    private var _binding: FragmentFoodDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setCurrentMealName(args.mealName)
        initUI()
    }

    private fun initUI() {
        initToolbar()
        initUIState()
        initNavigation()
    }

    private fun initToolbar() {
        binding.buttonClose.setOnClickListener {
            super.dismiss()
        }
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
        val viewPager = binding.viewPagerFood
        viewPager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.search)
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_search)
                }
                1 -> {
                    tab.text = getString(R.string.favourites)
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_favourite)
                }
            }
        }.attach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}
