package es.upm.macroscore.presentation.home.feed

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upm.macroscore.data.MealRepository
import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.domain.usecase.AddFoodUseCase
import es.upm.macroscore.domain.usecase.GetFoodsUseCase
import es.upm.macroscore.presentation.model.FoodUIModel
import es.upm.macroscore.presentation.model.MealUIModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    mealRepository: MealRepository,
    private val getFoodsUseCase: GetFoodsUseCase,
    private val addFoodUseCase: AddFoodUseCase
) : ViewModel() {

    private var _mealList = mealRepository.mealList
    val mealList: StateFlow<List<MealUIModel>> = _mealList.map {
        it.toMealUIModel()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    private var _currentMealName = MutableStateFlow("")
    private val currentMealName: StateFlow<String> = _currentMealName

    private val _closeDialogEvent = MutableSharedFlow<Unit>(replay = 0)
    val isReadyToDismiss: SharedFlow<Unit> = _closeDialogEvent

    private val _currentDay = MutableStateFlow<Long?>(null)
    val currentDay: StateFlow<Long?> = _currentDay

    init {
        _currentDay.value = Calendar.getInstance().time.time

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
