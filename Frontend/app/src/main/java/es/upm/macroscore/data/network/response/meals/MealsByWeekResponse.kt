package es.upm.macroscore.data.network.response.meals

import com.google.gson.annotations.SerializedName
import es.upm.macroscore.data.network.response.signup.UserProfileResponse
import es.upm.macroscore.domain.model.MealsByWeekModel

data class MealsByWeekResponse (
    @SerializedName("meals") val mealList: List<MealByDateResponse>,
    @SerializedName("user_profile") val userProfile: UserProfileResponse
) {

    fun toDomain() = MealsByWeekModel(
        mealList = this.mealList.map { meal -> meal.toDomain() },
        userProfile = this.userProfile.toDomain()
    )
}