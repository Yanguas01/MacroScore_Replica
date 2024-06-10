package es.upm.macroscore.ui.home.statistics

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import es.upm.macroscore.R
import es.upm.macroscore.databinding.ItemViewPagerMacrosBinding
import es.upm.macroscore.domain.model.DailyIntakeModel
import es.upm.macroscore.domain.model.NutritionalNeedsModel

class MacrosViewPagerViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView) {

    private val binding = ItemViewPagerMacrosBinding.bind(itemView)
    private lateinit var context: Context

    fun bind(dailyIntake: DailyIntakeModel, nutritionalNeeds: NutritionalNeedsModel) {
        context = itemView.rootView.context

        initWeekDayTextView()
        initCarbsPieChart(dailyIntake.totalCarbs, nutritionalNeeds.dailyCarbs)
        initProtsPieChart(dailyIntake.totalProts, nutritionalNeeds.dailyProts)
        initFatsPieChart(dailyIntake.totalFats, nutritionalNeeds.dailyFats)
    }

    private fun initWeekDayTextView() {
        val daysOfWeek = arrayOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        binding.textViewDay.text = daysOfWeek[bindingAdapterPosition]
    }

    private fun initCarbsPieChart(totalCarbs: Double, dailyCarbs: Double) {
        ObjectAnimator.ofFloat(binding.pieChartCarbs, "progress", 0f, totalCarbs.toFloat()).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            start()
        }
        binding.textViewCarbsSummarize.text = context.getString(R.string.carbs_summarize, totalCarbs, dailyCarbs)
    }

    private fun initProtsPieChart(totalProts: Double, dailyProts: Double) {
        ObjectAnimator.ofFloat(binding.pieChartProts, "progress", 0f, totalProts.toFloat()).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            start()
        }
        binding.textViewProtsSummarize.text = context.getString(R.string.prots_summarize, totalProts, dailyProts)
    }

    private fun initFatsPieChart(totalFats: Double, dailyFats: Double) {
        ObjectAnimator.ofFloat(binding.pieChartFats, "progress", 0f, totalFats.toFloat()).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            start()
        }
        binding.textViewFatsSummarize.text = context.getString(R.string.fats_summarize, totalFats, dailyFats)
    }





























}
