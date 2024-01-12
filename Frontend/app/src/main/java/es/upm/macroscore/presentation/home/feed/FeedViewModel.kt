package es.upm.macroscore.presentation.home.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upm.macroscore.data.MealRepository
import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.domain.usecase.AddFoodUseCase
import es.upm.macroscore.domain.usecase.GetFoodsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    mealRepository: MealRepository,
    private val getFoodsUseCase: GetFoodsUseCase,
    private val addFoodUseCase: AddFoodUseCase
) : ViewModel() {

    private var _mealList = mealRepository.mealList
    val mealList: StateFlow<List<MealModel>> = _mealList

    private var _currentMealName = MutableStateFlow("")
    val currentMealName: StateFlow<String> = _currentMealName

    private val _closeDialogEvent = MutableSharedFlow<Unit>(replay = 0)
    val isReadyToDismiss: SharedFlow<Unit> = _closeDialogEvent

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