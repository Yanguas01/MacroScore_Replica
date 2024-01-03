package es.upm.macroscore.presentation.home.feed

sealed class MealState {
    data object Empty: MealState()
    data object Expanded: MealState()
    data object Collapsed: MealState()
}