package es.upm.macroscore.presentation.home.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(): ViewModel() {

    private var calendar = Calendar.getInstance()

    private val _weekNumber = MutableStateFlow<Pair<Int, Int>?>(null)
    val weekNumber: MutableStateFlow<Pair<Int, Int>?> = _weekNumber

    private val _weekRange = MutableStateFlow<Pair<String, String>?>(null)
    val weekRange: StateFlow<Pair<String, String>?> = _weekRange

    init {
        _weekNumber.value = Pair(calendar.get(Calendar.WEEK_OF_YEAR), calendar.get(Calendar.YEAR))
        updateWeekRange(0)
    }

    fun nextWeek() {
        updateWeekRange(1)
        _weekNumber.value = Pair(calendar.get(Calendar.WEEK_OF_YEAR), calendar.get(Calendar.YEAR))
    }

    fun previousWeek() {
        updateWeekRange(-1)
        _weekNumber.value = Pair(calendar.get(Calendar.WEEK_OF_YEAR), calendar.get(Calendar.YEAR))
    }

    private fun updateWeekRange(weekOffset: Int) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
            calendar.firstDayOfWeek = Calendar.MONDAY
            calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            val startOfWeek = dateFormat.format(calendar.time)

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            val endOfWeek = dateFormat.format(calendar.time)

            _weekRange.value = Pair(startOfWeek, endOfWeek)
        }
    }
}
