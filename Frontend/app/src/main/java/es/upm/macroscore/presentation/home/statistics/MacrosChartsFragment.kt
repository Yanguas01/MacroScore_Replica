package es.upm.macroscore.presentation.home.statistics

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import es.upm.macroscore.databinding.FragmentMacrosChartsBinding

class MacrosChartsFragment : Fragment() {

    companion object {
        private const val ARG_DAY_OF_WEEK = "day_of_week"

        fun newInstance(dayOfWeek: Int): MacrosChartsFragment {
            val fragment = MacrosChartsFragment()
            val args = Bundle().apply {
                putInt(ARG_DAY_OF_WEEK, dayOfWeek)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentMacrosChartsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMacrosChartsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initWeekDayTextView()
        initCarbsPieChart()
        initProtsPieChart()
        initFatsPieChart()
    }

    private fun initWeekDayTextView() {
        val daysOfWeek = arrayOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        binding.textViewDay.text = daysOfWeek[arguments?.getInt(ARG_DAY_OF_WEEK)!!]
    }

    private fun initCarbsPieChart() {
        ObjectAnimator.ofFloat(binding.pieChartCarbs, "progress", 0f, 89f).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    private fun initProtsPieChart() {
        ObjectAnimator.ofFloat(binding.pieChartProts, "progress", 0f, 117f).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    private fun initFatsPieChart() {
        ObjectAnimator.ofFloat(binding.pieChartFats, "progress", 0f, 95f).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            start()
        }
    }
}
