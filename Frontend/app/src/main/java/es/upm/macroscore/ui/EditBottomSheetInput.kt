package es.upm.macroscore.ui

sealed class EditBottomSheetInput {
    data class StringInput(val value: String) : EditBottomSheetInput()
    data class DoubleInput(val value: Double) : EditBottomSheetInput()
}