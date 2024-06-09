package es.upm.macroscore.ui.home.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upm.macroscore.core.exceptions.MealAlreadySavedException
import es.upm.macroscore.domain.usecase.AddFoodToMealUseCase
import es.upm.macroscore.domain.usecase.AddMealUseCase
import es.upm.macroscore.domain.usecase.DeleteFoodUseCase
import es.upm.macroscore.domain.usecase.DeleteMealUseCase
import es.upm.macroscore.domain.usecase.EditFoodWeightUseCase
import es.upm.macroscore.domain.usecase.GetFoodsByPatternUseCase
import es.upm.macroscore.domain.usecase.GetMealsByDateUseCase
import es.upm.macroscore.domain.usecase.RenameMealUseCase
import es.upm.macroscore.domain.usecase.ReorderMealsUseCase
import es.upm.macroscore.ui.home.feed.meal.ErrorCodes
import es.upm.macroscore.ui.mappers.toUIModel
import es.upm.macroscore.ui.model.DateUIModel
import es.upm.macroscore.ui.model.FoodUIModel
import es.upm.macroscore.ui.model.MealUIModel
import es.upm.macroscore.ui.request.AddFoodRequest
import es.upm.macroscore.ui.request.MealRequest
import es.upm.macroscore.ui.request.OrderedMealRequest
import es.upm.macroscore.ui.states.OnlineOperationState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getMealsByDateUseCase: GetMealsByDateUseCase,
    private val addMealUseCase: AddMealUseCase,
    private val renameMealUseCase: RenameMealUseCase,
    private val deleteMealUseCase: DeleteMealUseCase,
    private val reorderMealsUseCase: ReorderMealsUseCase,
    private val getFoodsByPatternUseCase: GetFoodsByPatternUseCase,
    private val addFoodToMealUseCase: AddFoodToMealUseCase,
    private val editFoodWeightUseCase: EditFoodWeightUseCase,
    private val deleteFoodUseCase: DeleteFoodUseCase
) : ViewModel() {

    private val _calendar by lazy { MutableStateFlow<Calendar>(Calendar.getInstance()) }
    private val _currentDate = MutableStateFlow<DateUIModel?>(null)

    val currentDate: StateFlow<DateUIModel?> = _currentDate

    private val _mealList = MutableStateFlow(emptyList<MealUIModel>())
    val mealList: StateFlow<List<MealUIModel>> = _mealList

    private val _feedActionState: MutableStateFlow<OnlineOperationState> =
        MutableStateFlow(OnlineOperationState.Idle)
    val feedActionState: StateFlow<OnlineOperationState> = _feedActionState

    private val _currentMealId: MutableStateFlow<String> = MutableStateFlow("")

    private val _mealDialogState: MutableStateFlow<OnlineOperationState> =
        MutableStateFlow(OnlineOperationState.Idle)
    val mealDialogState: StateFlow<OnlineOperationState> = _mealDialogState

    private val _patternFlow = MutableStateFlow("")
    val foods: Flow<PagingData<FoodUIModel>> = _patternFlow
        .debounce(300)
        .filter {
            it.length > 2
        }
        .distinctUntilChanged()
        .flatMapLatest { pattern ->
            getFoodsByPatternUseCase(pattern)
        }
        .map { pagingData ->
            pagingData.map {
                it.toUIModel()
            }
        }
        .cachedIn(viewModelScope)

    private val _closeBottomSheetEvent = MutableSharedFlow<Unit>(replay = 0)
    val closeBottomSheetEvent: SharedFlow<Unit> = _closeBottomSheetEvent

    private val _closeFoodBottomSheetEvent = MutableSharedFlow<Unit>(replay = 0)
    val closeFoodBottomSheetEvent: SharedFlow<Unit> = _closeFoodBottomSheetEvent

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
        _feedActionState.update { OnlineOperationState.Loading }

        job = viewModelScope.launch {

            val locale = Locale("es", "ES")
            _calendar.value.add(Calendar.DAY_OF_YEAR, offset)

            val dayOfWeek = SimpleDateFormat("EEEE", locale).format(_calendar.value.time)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
            val dayOfMonth = _calendar.value.get(Calendar.DAY_OF_MONTH)
            val month = SimpleDateFormat("MMM", locale).format(_calendar.value.time)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

            _currentDate.update { DateUIModel(dayOfWeek, dayOfMonth, month) }


            getMealsByDateUseCase(
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(_calendar.value.time)
            )
                .map { result ->
                    result.map { meal ->
                        meal.toUIModel()
                    }
                }
                .onSuccess { list ->
                    _mealList.value = list.sortedBy { it.index }
                    _feedActionState.update { OnlineOperationState.Success }
                }
                .onFailure { exception ->
                    handleException(_feedActionState, exception)
                }
        }
    }

    fun addMeal(name: String, saveMeal: Boolean) {
        if (name !in _mealList.value.map { meal -> meal.name }) {
            _mealDialogState.update { OnlineOperationState.Loading }
            viewModelScope.launch {
                addMealUseCase(
                    MealRequest(
                        name = name,
                        datetime = SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(_calendar.value.time),
                        saveMeal = saveMeal
                    )
                )
                    .onSuccess {
                        val updatedList = _mealList.value + it.toUIModel()
                        _mealList.value = updatedList
                        _mealDialogState.update { OnlineOperationState.Success }
                        _mealDialogState.update { OnlineOperationState.Idle }
                    }
                    .onFailure { exception ->
                        handleException(_mealDialogState, exception)
                    }
            }
        } else {
            _mealDialogState.update {
                OnlineOperationState.Error(
                    "Ya existe una comida con ese nombre",
                    ErrorCodes.ERROR_ID_MEAL_ALREADY_EXISTS
                )
            }
        }
    }

    fun renameMeal(mealId: String, newName: String) {
        if (newName !in _mealList.value.map { meal -> meal.name }) {
            viewModelScope.launch {
                renameMealUseCase(mealId, newName)
                    .onSuccess {
                        _mealList.update { list ->
                            val index = list.indexOfFirst { it.id == mealId }
                            if (index != -1) {
                                val meal = list[index].copy(name = it.newName)
                                list.toMutableList().apply {
                                    this[index] = meal
                                }
                            } else {
                                list
                            }
                        }
                        _closeBottomSheetEvent.emit(Unit)
                    }
                    .onFailure { exception ->
                        handleException(_feedActionState, exception)
                    }
            }
        } else {
            _feedActionState.update {
                OnlineOperationState.Error(
                    "Ya existe una comida con ese nombre",
                    ErrorCodes.ERROR_ID_MEAL_ALREADY_EXISTS
                )
            }
        }
    }

    private fun handleException(
        state: MutableStateFlow<OnlineOperationState>,
        exception: Throwable
    ) {
        when (exception) {
            is IOException -> {
                state.update { OnlineOperationState.Error("Error de red: ${exception.message}") }
            }

            is HttpException -> {
                state.update { OnlineOperationState.Error("Error HTTP: ${exception.message}") }
            }

            is CancellationException -> {
                Log.e("FeedViewModel", "Cancelled Job")
            }

            is MealAlreadySavedException -> {
                state.update {
                    OnlineOperationState.Error(
                        "Ya tienes guardada una comida con ese nombre",
                        ErrorCodes.ERROR_ID_MEAL_IN_TEMPLATE
                    )
                }
            }

            else -> {
                state.update { OnlineOperationState.Error("Unknown Error: ${exception.message}") }
            }
        }
    }

    fun deleteMeal(mealId: String) {
        viewModelScope.launch {
            deleteMealUseCase(mealId)
                .onSuccess {
                    _mealList.update { list -> list
                        .filterNot { it.id == mealId }
                        .mapIndexed { i, meal -> meal.copy(index = i) }
                    }
                    _feedActionState.update { OnlineOperationState.Success }
                }
                .onFailure { exception ->
                    handleException(_feedActionState, exception)
                }
        }
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val reorderedList = _mealList.value.toMutableList().apply {
            val item = removeAt(fromPosition)
            add(toPosition, item)
            forEachIndexed { index, it ->
                it.index = index
            }
        }
        _mealList.update { reorderedList }
        reorderMeals()
    }

    private fun reorderMeals() {
        viewModelScope.launch {
            val requestList =
                _mealList.value.map { meal -> OrderedMealRequest(meal.id, meal.name, meal.index) }
            reorderMealsUseCase(
                requestList
            )
                .onFailure { exception ->
                    handleException(_feedActionState, exception)
                }
        }
    }

    fun addFoodToMeal(foodId: String, weight: Double) {
        viewModelScope.launch {
            addFoodToMealUseCase(
                _currentMealId.value,
                AddFoodRequest(foodId, weight)
            )
                .onSuccess { food ->
                    _mealList.update {
                        it.map { meal ->
                            if (meal.id == _currentMealId.value) {
                                meal.state = MealState.EXPANDED
                                meal.copy(items = meal.items + food.toUIModel())
                            } else {
                                meal
                            }
                        }
                    }
                    _closeFoodBottomSheetEvent.emit(Unit)
                }
                .onFailure { exception ->
                    handleException(_feedActionState, exception)
                }
        }
    }

    fun editFoodWeight(mealId: String, foodId: String, newWeight: Double) {
        viewModelScope.launch {
            editFoodWeightUseCase(
                mealId, foodId, newWeight
            )
                .onSuccess { editFoodWeightModel ->
                    _mealList.update {
                        it.map { meal ->
                            if (editFoodWeightModel.mealId == meal.id) {
                                val items = meal.items.map { food ->
                                    if (editFoodWeightModel.foodId == food.id) {
                                        food.copy(weight = editFoodWeightModel.weight)
                                    } else {
                                        food
                                    }
                                }
                                meal.copy(items = items)
                            } else {
                                meal
                            }
                        }
                    }
                    _closeBottomSheetEvent.emit(Unit)
                }
                .onFailure { exception ->
                    handleException(_feedActionState, exception)
                }
        }
    }

    fun deleteFood(mealId: String, foodId: String) {
        viewModelScope.launch {
            deleteFoodUseCase(
                mealId, foodId
            )
                .onSuccess {
                    _mealList.update {
                        it.map { meal ->
                            if (meal.id == mealId) {
                                val items = meal.items.filterNot { food ->
                                    food.id == foodId
                                }
                                if (items.isEmpty()) {
                                    meal.copy(state = MealState.EMPTY, items = items)
                                } else {
                                    meal.copy(items = items)
                                }
                            } else {
                                meal
                            }
                        }
                    }
                }
                .onFailure { exception ->
                    handleException(_feedActionState, exception)
                }
        }
    }

    fun setPattern(pattern: String) {
        _patternFlow.value = pattern.trimEnd()
    }

    fun resetMealDialogState() {
        _mealDialogState.update { OnlineOperationState.Idle }
    }

    fun setCurrentMealId(id: String) {
        _currentMealId.value = id
    }
}