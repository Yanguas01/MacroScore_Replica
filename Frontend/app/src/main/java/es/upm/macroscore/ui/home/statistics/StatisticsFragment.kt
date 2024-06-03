package es.upm.macroscore.presentation.home.statistics

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.transition.Visibility
import androidx.viewpager2.widget.ViewPager2
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentStatisticsBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Calendar


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
        Handler(Looper.getMainLooper()).postDelayed({
            initUI()
        }, 2000)
    }

    private fun initUI() {
        binding.shimmerLayoutEnergyChart.isVisible = false
        binding.shimmerLayoutMacrosCharts.isVisible = false
        binding.barChartKcal.isVisible = true
        binding.viewPagerMacros.isVisible = true
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
        binding.layoutToolbarTitle.setOnClickListener {
            initDatePicker()
        }
    }

    private fun initDatePicker() {
        val builder = MaterialDatePicker.Builder
            .datePicker()
            .setTheme(R.style.DatePicker)
            .setTitleText("Selecciona una fecha")
        val materialDatePicker = builder.build()

        materialDatePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selection
        }

        materialDatePicker.show(parentFragmentManager, "tag")
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

        barChart.setPinchZoom(false)
        barChart.isDoubleTapToZoomEnabled = false
        barChart.isDragEnabled = false
        barChart.isScaleXEnabled = false
        barChart.isScaleYEnabled = false

        val consumedKcal = listOf(2000f, 900f, 2200f, 2100f, 2300f, 2000f, 1950f)
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

        barChart.animateY(1000, Easing.EaseOutQuad)

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    binding.viewPagerMacros.currentItem = e.x.toInt()
                }
            }

            override fun onNothingSelected() {
                Log.i("Bar Selection", "Nothing selected.")
            }
        })
    }

    private fun initPieCharts() {
        initPieChartButtons()
        binding.viewPagerMacros.adapter = MacrosViewPagerAdapter(this)
        binding.viewPagerMacros.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonsState(position)
                binding.barChartKcal.highlightValue(position.toFloat(), 0)
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
