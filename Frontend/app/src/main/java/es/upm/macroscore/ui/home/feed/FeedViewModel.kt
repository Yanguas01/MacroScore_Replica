package es.upm.macroscore.ui.home.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upm.macroscore.core.exceptions.MealAlreadySavedException
import es.upm.macroscore.domain.usecase.AddMealUseCase
import es.upm.macroscore.domain.usecase.DeleteMealUseCase
import es.upm.macroscore.domain.usecase.GetMealsByDateUseCase
import es.upm.macroscore.domain.usecase.RenameMealUseCase
import es.upm.macroscore.domain.usecase.ReorderMealsUseCase
import es.upm.macroscore.ui.mappers.toUIModel
import es.upm.macroscore.ui.model.DateUIModel
import es.upm.macroscore.ui.model.FoodUIModel
import es.upm.macroscore.ui.model.MealUIModel
import es.upm.macroscore.ui.request.OrderedMealRequest
import es.upm.macroscore.ui.request.MealRequest
import es.upm.macroscore.ui.states.OnlineOperationState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getMealsByDateUseCase: GetMealsByDateUseCase,
    private val addMealUseCase: AddMealUseCase,
    private val renameMealUseCase: RenameMealUseCase,
    private val deleteMealUseCase: DeleteMealUseCase,
    private val reorderMealsUseCase: ReorderMealsUseCase
) : ViewModel() {

    private val _calendar by lazy { MutableStateFlow<Calendar>(Calendar.getInstance()) }

    private val _currentDate = MutableStateFlow<DateUIModel?>(null)
    val currentDate: StateFlow<DateUIModel?> = _currentDate

    private val _mealList = MutableStateFlow(emptyList<MealUIModel>())
    val mealList: StateFlow<List<MealUIModel>> = _mealList

    private val _feedActionState: MutableStateFlow<OnlineOperationState> =
        MutableStateFlow(OnlineOperationState.Idle)
    val feedActionState: StateFlow<OnlineOperationState> = _feedActionState

    private var _currentMealName = MutableStateFlow("")
    private val currentMealName: StateFlow<String> = _currentMealName

    private val _foods = MutableStateFlow<PagingData<FoodUIModel>>(PagingData.empty())
    val foods get(): StateFlow<PagingData<FoodUIModel>> = _foods

    private val _closeDialogEvent = MutableSharedFlow<Unit>(replay = 0)
    val isReadyToDismiss: SharedFlow<Unit> = _closeDialogEvent

    private val _closeBottomSheetEvent = MutableSharedFlow<Unit>(replay = 0)
    val closeBottomSheetEvent: SharedFlow<Unit> = _closeBottomSheetEvent

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
                    handleException(exception)
                }
        }
    }

    fun addMeal(name: String, saveMeal: Boolean) {
        if (name !in _mealList.value.map { meal -> meal.name }) {
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
                        closeDialog()
                        val updatedList = _mealList.value + it.toUIModel()
                        _mealList.value = updatedList
                    }
                    .onFailure { exception ->
                        handleException(exception)
                    }
            }
        } else {
            _feedActionState.update { OnlineOperationState.Error("Ya existe una comida con ese nombre") }
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
                        handleException(exception)
                    }
            }
        } else {
            _feedActionState.update { OnlineOperationState.Error("Ya existe una comida con ese nombre") }
        }
    }

    private fun handleException(exception: Throwable) {
        when (exception) {
            is IOException -> {
                _feedActionState.update { OnlineOperationState.Error("Error de red: ${exception.message}") }
            }

            is HttpException -> {
                _feedActionState.update { OnlineOperationState.Error("Error HTTP: ${exception.message}") }
            }

            is CancellationException -> {
                Log.e("FeedViewModel", "Cancelled Job")
            }

            is MealAlreadySavedException -> {
                _feedActionState.update {
                    OnlineOperationState.Error("Ya tienes guardada una comida con ese nombre")
                }
            }

            else -> {
                _feedActionState.update { OnlineOperationState.Error("Unknown Error: ${exception.message}") }
            }
        }
    }

    fun deleteMeal(mealId: String) {
        viewModelScope.launch {
            deleteMealUseCase(mealId)
                .onSuccess {
                    _mealList.update { list -> list.filterNot { it.id == mealId } }
                }
                .onFailure { exception ->
                    handleException(exception)
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
        _mealList.value = reorderedList
        reorderMeals()
    }

    private fun reorderMeals() {
        viewModelScope.launch {
            val requestList = _mealList.value.map { meal -> OrderedMealRequest(meal.id, meal.name, meal.index) }
            reorderMealsUseCase(
                requestList
            )
                .onFailure { exception ->
                    handleException(exception)
                }
        }
    }

    fun getFoods() {
        viewModelScope.launch {
            val pagingSource = object : PagingSource<Int, FoodUIModel>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FoodUIModel> {
                    val page = params.key ?: 1
                    val response =
                }

                override fun getRefreshKey(state: PagingState<Int, FoodUIModel>): Int? {
                   /* return state.anchorPosition?.let { anchorPosition ->
                        state.closestItemToPosition(anchorPosition)?.prevKey
                    }*/
                }
            }
        }
    }

    fun setCurrentMealName(name: String) {
        _currentMealName.value = name
    }

    private fun closeDialog() {
        viewModelScope.launch {
            _closeDialogEvent.emit(Unit)
        }
    }
}