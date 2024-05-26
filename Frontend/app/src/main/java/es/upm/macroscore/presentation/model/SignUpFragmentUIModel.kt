package es.upm.macroscore.presentation.model

import android.provider.ContactsContract.CommonDataKinds.Email

data class SignUpFragmentUIModel(
    val username: String,
    val email: String,
    val password: String
)
