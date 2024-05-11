package es.upm.macroscore.data

import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.presentation.model.MealUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepository @Inject constructor() {

    private val _mealList = MutableStateFlow<List<MealModel>>(emptyList())
    val mealList: StateFlow<List<MealModel>> = _mealList




    fun updateList(mealModel: MealModel) {
        _mealList.value = _mealList.value.plus(mealModel)
    }

    fun updateMeal(foodModel: FoodModel, mealName: String) {
        val updateMeal = _mealList.value.map { mealModel ->
            if (mealModel.name == mealName) mealModel.copy(foods = mealModel.foods.plus(foodModel)) else mealModel
        }
        _mealList.value = updateMeal
    }
}



