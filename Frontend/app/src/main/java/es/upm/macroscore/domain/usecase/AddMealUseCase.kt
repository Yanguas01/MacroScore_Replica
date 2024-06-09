package es.upm.macroscore.domain.usecase

import android.util.Log
import es.upm.macroscore.core.exceptions.MealAlreadySavedException
import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.domain.repositories.MealRepository
import es.upm.macroscore.domain.repositories.UserRepository
import es.upm.macroscore.ui.request.MealRequest
import javax.inject.Inject

class AddMealUseCase @Inject constructor(
    private val mealRepository: MealRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(
        mealRequest: MealRequest
    ): Result<MealModel> {
        return try {
            if (mealRequest.saveMeal) {
                val userOrderMealResult = userRepository.getUserOrderMeal()
                if (userOrderMealResult.isSuccess) {
                    Log.d("AddMealUseCase", "User Order Meal: ${userOrderMealResult.getOrThrow().toString()}")
                    val userOrderMeal = userOrderMealResult.getOrThrow()
                    if (mealRequest.name !in userOrderMeal.orderMeal) {
                        mealRepository.addMeal(mealRequest)
                    } else {
                        Result.failure(MealAlreadySavedException("The meal is already saved in the user's meals template"))
                    }
                } else {
                    Result.failure(userOrderMealResult.exceptionOrNull() ?: Exception("Unknown error"))
                }
            } else {
                mealRepository.addMeal(mealRequest)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}