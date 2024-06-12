package es.upm.macroscore.ui.home.statistics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upm.macroscore.domain.model.DailyIntakeModel
import es.upm.macroscore.domain.model.NutritionalNeedsModel
import es.upm.macroscore.domain.usecase.GetMealsByWeekUseCase
import es.upm.macroscore.ui.states.OnlineOperationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getMealsByWeekUseCase: GetMealsByWeekUseCase
): ViewModel() {

    private var calendar = Calendar.getInstance()

    private val _weekNumber = MutableStateFlow<Pair<Int, Int>?>(null)
    val weekNumber: StateFlow<Pair<Int, Int>?> = _weekNumber

    private val _weekRange = MutableStateFlow<Pair<String, String>?>(null)
    val weekRange: StateFlow<Pair<String, String>?> = _weekRange

    private val _weekDays = MutableStateFlow<List<String>>(emptyList())
    val weekDays get() : StateFlow<List<String>> = _weekDays

    private val _statisticsState = MutableStateFlow<OnlineOperationState>(OnlineOperationState.Idle)
    val statisticState get(): StateFlow<OnlineOperationState> = _statisticsState

    private val _statisticsData = MutableStateFlow<Pair<NutritionalNeedsModel?, Map<String, DailyIntakeModel>>>(Pair(null, emptyMap()))
    val statisticsData get(): StateFlow<Pair<NutritionalNeedsModel?, Map<String, DailyIntakeModel>>> = _statisticsData

    private var job: Job? = null

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
        job?.cancel()
        _statisticsState.update { OnlineOperationState.Loading }

        job = viewModelScope.launch {

            withContext(Dispatchers.IO) {

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val requestFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                calendar.firstDayOfWeek = Calendar.MONDAY
                calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)

                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                val startOfWeek = dateFormat.format(calendar.time)
                val startOfWeekRequest = requestFormat.format(calendar.time)

                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                val endOfWeek = dateFormat.format(calendar.time)
                val endOfWeekRequest = requestFormat.format(calendar.time)

                _weekRange.value = Pair(startOfWeek, endOfWeek)
                _weekDays.value = getDatesBetween(startOfWeekRequest, endOfWeekRequest)

                getMealsByWeekUseCase(
                    startOfWeekRequest, endOfWeekRequest
                )
                    .onSuccess { data ->
                        _statisticsData.update { data }
                        _statisticsState.update { OnlineOperationState.Success }
                    }
                    .onFailure { exception ->
                        handleException(exception)
                    }
            }
        }
    }

    private fun getDatesBetween(startDateStr: String, endDateStr: String): List<String> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()

        startDate.time = dateFormat.parse(startDateStr)!!
        endDate.time = dateFormat.parse(endDateStr)!!

        val dates = mutableListOf<String>()
        val currentDate = startDate.clone() as Calendar

        while (!currentDate.after(endDate)) {
            dates.add(dateFormat.format(currentDate.time))
            currentDate.add(Calendar.DATE, 1)
        }

        return dates
    }

    private fun handleException(exception: Throwable) {
        when (exception) {
            is IOException -> {
                _statisticsState.update { OnlineOperationState.Error("Error de red: ${exception.message}") }
            }

            is HttpException -> {
                _statisticsState.update { OnlineOperationState.Error("Error HTTP: ${exception.message}") }
            }

            is CancellationException -> {
                Log.e("StatisticsViewModel", "Cancelled Job")
            }

            else -> {
                _statisticsState.update { OnlineOperationState.Error("Unknown Error: ${exception.message}") }
            }
        }
    }
}
