package es.upm.macroscore.ui.home.feed

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.domain.usecase.AddMealUseCase
import es.upm.macroscore.domain.usecase.GetMealsByDateUseCase
import es.upm.macroscore.ui.mappers.toUIModel
import es.upm.macroscore.ui.model.DateUIModel
import es.upm.macroscore.ui.model.MealUIModel
import es.upm.macroscore.ui.request.MealRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getMealsByDateUseCase: GetMealsByDateUseCase,
    private val addMealUseCase: AddMealUseCase
) : ViewModel() {

    private var calendar = java.util.Calendar.getInstance()

    private val _currentDate = MutableStateFlow<DateUIModel?>(null)
    val currentDate: StateFlow<DateUIModel?> = _currentDate

    private var _mealList = MutableStateFlow(emptyList<MealUIModel>())
    val mealList: StateFlow<List<MealUIModel>> = _mealList

    private var _currentMealName = MutableStateFlow("")
    private val currentMealName: StateFlow<String> = _currentMealName

    private val _closeDialogEvent = MutableSharedFlow<Unit>(replay = 0)
    val isReadyToDismiss: SharedFlow<Unit> = _closeDialogEvent

    private var job: Job? = null

    init {
        updateDate(0)
    }

    fun previousDay() {
        updateDate(-1)
    }

    fun nextDay() {
        updateDate(1)
    }

    private fun updateDate(offset: Int) {
        job?.cancel()
        job = viewModelScope.launch {
            val locale = Locale("es", "ES")
            calendar.add(Calendar.DAY_OF_YEAR, offset)

            val dayOfWeek = SimpleDateFormat("EEEE", locale).format(calendar.time)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val month = SimpleDateFormat("MMM", locale).format(calendar.time)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

            _currentDate.update { DateUIModel(dayOfWeek, dayOfMonth, month) }

            getMealsByDateUseCase(
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    calendar.time
                )
            )
                .map { result ->
                    result.map { meal ->
                        meal.toUIModel()
                    }
                }
                .onSuccess {
                    _mealList.update { it }
                }
        }
    }

    fun addMeal(name: String, saveMeal: Boolean) {
        viewModelScope.launch {
            Log.d("FeedViewModel", "addMealUseCase")
            addMealUseCase(
                MealRequest(
                    name = name,
                    datetime = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time),
                    saveMeal = saveMeal
                )
            )
                .onSuccess {
                    Log.d("FeedViewModel", it.toString())
                    Log.d("FeedViewModel", it.toUIModel().toString())
                    _mealList.update { list -> list + it.toUIModel() }
                    Log.d("FeedViewModel", _mealList.toString())
                }
        }
    }

    fun setCurrentMealName(name: String) {
        _currentMealName.value = name
    }

    fun closeDialog() {
        viewModelScope.launch {
            _closeDialogEvent.emit(Unit)
        }
    }
}