package es.upm.macroscore.presentation.home.profile.enums

import android.text.InputType
import es.upm.macroscore.R

enum class TextInputLayoutInfo(val inputType: Int, val hint: Int, val startIcon: Int) {
    Username(InputType.TYPE_CLASS_TEXT, R.string.username, R.drawable.ic_profile),
    Email(InputType.TYPE_CLASS_TEXT, R.string.email, R.drawable.ic_email),
    Height(InputType.TYPE_CLASS_NUMBER, R.string.height, R.drawable.ic_height),
    Weight(InputType.TYPE_CLASS_NUMBER, R.string.weight, R.drawable.ic_weight),
    Age(InputType.TYPE_CLASS_NUMBER, R.string.age, R.drawable.ic_age)
}