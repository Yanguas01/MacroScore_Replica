package es.upm.macroscore.data.storage

import android.content.SharedPreferences
import javax.inject.Inject

class UserManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun getUserId(): String {
        return sharedPreferences.getString("user_id", "")!!
    }

    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString("user_id", userId).apply()
    }

    fun clearUserId() {
        sharedPreferences.edit().remove("user_id").apply()
    }
}