package es.upm.macroscore.ui.home.feed.food.adapter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upm.macroscore.domain.model.FoodModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    fun getFood(): List<FoodModel> {
        return emptyList()
    }

}