package es.upm.macroscore.data.network.dto.users

import com.google.gson.annotations.SerializedName

data class UpdatePasswordDTO (
    @SerializedName("old_password") val oldPassword: String,
    @SerializedName("new_password") val newPassword: String
)
