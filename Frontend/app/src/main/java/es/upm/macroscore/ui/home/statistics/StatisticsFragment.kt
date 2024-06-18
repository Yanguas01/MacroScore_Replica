package es.upm.macroscore.ui.home.statistics

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentStatisticsBinding
import es.upm.macroscore.domain.model.DailyIntakeModel
import es.upm.macroscore.domain.model.NutritionalNeedsModel
import es.upm.macroscore.ui.states.OnlineOperationState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar


@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private val viewModel by viewModels<StatisticsViewModel>()

    private val data: Pair<NutritionalNeedsModel?, Map<String, DailyIntakeModel>> = Pair(null, emptyMap())

    private var weekDays: List<String> = emptyList()

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
        viewModel.initializeData()
        initUI()
        initUIState()
    }

    private fun initUI() {
        initToolbar()
    }

    private fun initUIState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.statisticsData.collectLatest { data ->
                        Log.e("StatisticsFragment", "Collecting statistics Data: ${data.first.toString()}")
                        if (data.first != null) initGraphs(data)
                    }
                }
                launch {
                    viewModel.weekNumber.collect { week ->
                        week?.let { (weekNumber, year) ->
                            binding.textViewWeek.text = this@StatisticsFragment.requireContext()
                                .getString(R.string.week_number, weekNumber, year)
                        }
                    }
                }
                launch {
                    viewModel.weekRange.collect {
                        it?.let { (startOfWeek, endOfWeek) ->
                            binding.textViewWeekSummarize.text =
                                this@StatisticsFragment.requireContext()
                                    .getString(R.string.week_range, startOfWeek, endOfWeek)
                        }
                    }
                }
                launch {
                    viewModel.weekDays.collect {
                        Log.e("StatisticsFragment", weekDays.toString())
                        if (it.isNotEmpty()) weekDays = it
                    }
                }
                launch {
                    viewModel.statisticState.collect { state ->
                        Log.e("StatisticsFragment", state.toString())
                        handleState(state)
                    }
                }
            }
        }
    }

    private fun handleState(state: OnlineOperationState) {
        when (state) {
            is OnlineOperationState.Idle -> { }
            is OnlineOperationState.Loading -> {
                binding.shimmerLayoutEnergyChart.isVisible = true
                binding.shimmerLayoutMacrosCharts.isVisible = true
                binding.shimmerLayoutEnergyChart.startShimmer()
                binding.shimmerLayoutMacrosCharts.startShimmer()
                binding.barChartKcal.visibility = View.INVISIBLE
                binding.viewPagerMacros.isVisible = false
            }
            is OnlineOperationState.Success -> {
                binding.shimmerLayoutEnergyChart.stopShimmer()
                binding.shimmerLayoutMacrosCharts.stopShimmer()
                binding.shimmerLayoutEnergyChart.isVisible = false
                binding.shimmerLayoutMacrosCharts.isVisible = false
                binding.barChartKcal.isVisible = true
                binding.viewPagerMacros.isVisible = true
            }
            is OnlineOperationState.Error -> {
                binding.shimmerLayoutEnergyChart.stopShimmer()
                binding.shimmerLayoutMacrosCharts.stopShimmer()
                binding.shimmerLayoutEnergyChart.isVisible = false
                binding.shimmerLayoutMacrosCharts.isVisible = false
                binding.barChartKcal.isVisible = true
                binding.viewPagerMacros.isVisible = true
            }
        }
    }

    private fun initToolbar() {
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

    private fun initGraphs(data: Pair<NutritionalNeedsModel?, Map<String, DailyIntakeModel>>) {
        val completeDailyIntake = mutableMapOf<String, DailyIntakeModel>()
        for (day in weekDays) {
            completeDailyIntake[day] = data.second[day] ?: DailyIntakeModel(0.0, 0.0, 0.0, 0.0)
        }
        initBarChart(data.first!!.dailyKcal, completeDailyIntake)
        initPieCharts(data.first!!, completeDailyIntake)
    }

    private fun initBarChart(recommendedKcal: Double, dailyIntake: Map<String, DailyIntakeModel>) {
        val barChart = binding.barChartKcal

        barChart.setPinchZoom(false)
        barChart.isDoubleTapToZoomEnabled = false
        barChart.isDragEnabled = false
        barChart.isScaleXEnabled = false
        barChart.isScaleYEnabled = false

        val entries = dailyIntake.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.totalKcal.toFloat())
        }

        val dataSet = BarDataSet(entries, "Kcal consumidas")
        val barData = BarData(dataSet)
        barChart.data = barData

        val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(daysOfWeek)
        barChart.xAxis.setGranularity(1f)
        barChart.xAxis.isGranularityEnabled = true

        val limitLine = LimitLine(recommendedKcal.toFloat(), "Kcal recomendadas")
        barChart.axisLeft.addLimitLine(limitLine)

        barChart.description.isEnabled = false
        barChart.legend.isEnabled = true


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

        barChart.invalidate()
    }

    private fun initPieCharts(
        nutritionalNeeds: NutritionalNeedsModel,
        dailyIntakeMap: MutableMap<String, DailyIntakeModel>
    ) {
        initPieChartButtons()
        Log.e("StatisticsFragment", dailyIntakeMap.toString())
        val dailyIntakeList = dailyIntakeMap.entries.sortedBy { it.key }.map { Pair(it.value, nutritionalNeeds) }
        binding.viewPagerMacros.adapter = MacrosViewPagerAdapter(dailyIntakeList)
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetState()
    }
}
