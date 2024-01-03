package es.upm.macroscore.presentation.home.feed.food.adapter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.domain.usecase.GetFoodsUseCase
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val getFoodsUseCase: GetFoodsUseCase) : ViewModel() {

    fun getFood(): List<FoodModel> {
        return getFoodsUseCase()
    }

}