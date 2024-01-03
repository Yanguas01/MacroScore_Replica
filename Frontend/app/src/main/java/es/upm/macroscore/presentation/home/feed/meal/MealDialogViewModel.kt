package es.upm.macroscore.presentation.home.feed.meal

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.domain.usecase.SaveMealUseCase
import javax.inject.Inject

@HiltViewModel
class MealDialogViewModel @Inject constructor(private val saveMealUseCase: SaveMealUseCase) : ViewModel()  {

    fun saveMeal (name: String, isChecked: Boolean) {
        saveMealUseCase(MealModel(name, emptyList()))
    }
}