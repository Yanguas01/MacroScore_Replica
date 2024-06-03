package es.upm.macroscore.presentation.home.feed

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upm.macroscore.data.network.response.meals.MealByDateResponse
import es.upm.macroscore.data.repository.MealRepository
import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.domain.usecase.AddFoodUseCase
import es.upm.macroscore.domain.usecase.GetFoodsUseCase
import es.upm.macroscore.domain.usecase.GetMealsByDateUseCase
import es.upm.macroscore.presentation.model.DateUIModel
import es.upm.macroscore.presentation.model.FoodUIModel
import es.upm.macroscore.presentation.model.MealUIModel
import es.upm.macroscore.presentation.states.OnlineValidationFieldState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getMealsByDateUseCase: GetMealsByDateUseCase,
    private val getFoodsUseCase: GetFoodsUseCase,
    private val addFoodUseCase: AddFoodUseCase
) : ViewModel() {

    private var calendar = java.util.Calendar.getInstance()

    private val _currentDate = MutableStateFlow<DateUIModel?>(null)
    val currentDate: StateFlow<DateUIModel?> = _currentDate

    private var _mealList = MutableStateFlow(mutableListOf<MealByDateResponse>())
    val mealList: StateFlow<List<MealByDateResponse>> = _mealList

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

            getMealsByDateUseCase(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar))
                .onSuccess {
                    _mealList.update { it }
                }
        }
    }

    fun setCurrentMealName(name: String) {
        _currentMealName.value = name
    }

    fun addFood(foodModel: FoodModel) {
        addFoodUseCase(foodModel, currentMealName.value)
    }

    fun getFood(): List<FoodModel> {
        return getFoodsUseCase()
    }

    fun closeDialog() {
        viewModelScope.launch {
            _closeDialogEvent.emit(Unit)
        }
    }
}

private fun List<MealModel>.toMealUIModel(): List<MealUIModel> {
    return this.map {
        it.toMealUIModel()
    }
}

private fun MealModel.toMealUIModel(): MealUIModel {
    return MealUIModel(
        name = this.name,
        foods = this.foods.toFoodUIModel(),
        state = null
    )
}

private fun List<FoodModel>.toFoodUIModel(): List<FoodUIModel> {
    return this.map {
        it.toFoodUIModel()
    }
}

private fun FoodModel.toFoodUIModel(): FoodUIModel {
    return FoodUIModel(
        name = this.name,
        kcalPer100 = this.kcalPer100,
        carbsPer100 = this.carbsPer100,
        protsPer100 = this.protsPer100,
        fatsPer100 = this.fatsPer100
    )
}
