package es.upm.macroscore.presentation.home.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentStatisticsBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private val viewModel by viewModels<StatisticsViewModel>()

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initToolbar()
        initGraphs()
    }

    private fun initToolbar() {
        initToolbarState()
        binding.buttonNextWeek.setOnClickListener {
            viewModel.nextWeek()
        }
        binding.buttonPreviousWeek.setOnClickListener {
            viewModel.previousWeek()
        }
    }

    private fun initToolbarState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.weekNumber.collect { week ->
                        week?.let { (weekNumber, year) ->
                            binding.textViewWeek.text = this@StatisticsFragment.requireContext()
                                .getString(R.string.week_number, weekNumber, year)
                        }
                    }
                }
                launch {
                    viewModel.weekRange.collect { weekRange ->
                        weekRange?.let { (startOfWeek, endOfWeek) ->
                            binding.textViewWeekSummarize.text =
                                this@StatisticsFragment.requireContext()
                                    .getString(R.string.week_range, startOfWeek, endOfWeek)
                        }
                    }
                }
            }
        }
    }

    private fun initGraphs() {
        initBarChart()
        initPieCharts()
    }

    private fun initBarChart() {
        val barChart = binding.barChartKcal

        val consumedKcal = listOf(2000f, 1800f, 2200f, 2100f, 2300f, 2000f, 1950f)
        val entries = mutableListOf<BarEntry>()
        consumedKcal.forEachIndexed { index, kcal ->
            entries.add(BarEntry(index.toFloat(), kcal))
        }

        val dataSet = BarDataSet(entries, "Kcal consumidas")
        val barData = BarData(dataSet)

        barChart.data = barData

        barChart.xAxis.valueFormatter = object : ValueFormatter() {
            private val days =
                arrayOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return days.getOrNull(value.toInt()) ?: value.toString()
            }
        }

        val recommendedKcal = 2200f
        val limitLine = LimitLine(recommendedKcal, "Kcal recomendadas")
        barChart.axisLeft.addLimitLine(limitLine)

        barChart.description.isEnabled = false
        barChart.legend.isEnabled = true

        barChart.invalidate()
    }

    private fun initPieCharts() {
        initPieChartButtons()
        binding.viewPagerMacros.adapter = MacrosViewPagerAdapter(this)
        binding.viewPagerMacros.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonsState(position)
            }
        })
    }

    private fun initPieChartButtons() {
        binding.buttonPreviousDay.setOnClickListener { binding.viewPagerMacros.currentItem -= 1 }
        binding.buttonNextDay.setOnClickListener { binding.viewPagerMacros.currentItem += 1 }
    }

    private fun updateButtonsState(position: Int) {
        binding.buttonPreviousDay.isEnabled = position != 0
        binding.buttonNextDay.isEnabled = position != (binding.viewPagerMacros.adapter?.itemCount ?: 1) - 1
    }
}
