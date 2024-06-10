package es.upm.macroscore.data.network

import es.upm.macroscore.data.network.dto.meals.AddFoodDTO
import es.upm.macroscore.data.network.dto.meals.MealDTO
import es.upm.macroscore.data.network.dto.meals.OrderedMealDTO
import es.upm.macroscore.data.network.dto.signup.SignUpDTO
import es.upm.macroscore.data.network.response.foods.FoodListResponse
import es.upm.macroscore.data.network.response.foods.FoodResponse
import es.upm.macroscore.data.network.response.login.LogInResponse
import es.upm.macroscore.data.network.response.meals.AddFoodResponse
import es.upm.macroscore.data.network.response.meals.EditFoodWeightResponse
import es.upm.macroscore.data.network.response.signup.CheckEmailResponse
import es.upm.macroscore.data.network.response.meals.MealByDateResponse
import es.upm.macroscore.data.network.response.meals.MealsByWeekResponse
import es.upm.macroscore.data.network.response.meals.RenameMealResponse
import es.upm.macroscore.data.network.response.signup.CheckUsernameResponse
import es.upm.macroscore.data.network.response.signup.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MacroScoreApiService {

    @GET("users/check_email")
    suspend fun checkEmail(@Query("email") email: String): Response<CheckEmailResponse>

    @GET("users/check_username")
    suspend fun checkUsername(@Query("username") username: String): Response<CheckUsernameResponse>

    @POST("signup")
    suspend fun createNewUser(@Body signUpDTO: SignUpDTO): Response<SignUpResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun logUser(@Field("username") username: String, @Field("password") password: String, @Field("scope") scope: String): Response<LogInResponse>

    @GET("users/me")
    suspend fun getMyUser(): Response<SignUpResponse>

    @POST("refresh")
    suspend fun refreshToken(@Body refreshToken: String): Response<LogInResponse>

    @GET("meals")
    suspend fun getMealsByDate(@Query("target_date") targetDate: String): Response<List<MealByDateResponse>>

    @POST("meals")
    suspend fun addMeal(@Body mealDTO: MealDTO): Response<MealByDateResponse>

    @PATCH("meals/{meal_id}/rename")
    suspend fun renameMeal(@Path("meal_id") mealId: String, @Body newMealName: String): Response<RenameMealResponse>

    @DELETE("meals/{meal_id}")
    suspend fun deleteMeal(@Path("meal_id") mealId: String) : Response<Unit>

    @PATCH("meals/reorder")
    suspend fun reorderMeal(@Body orderedMealList: List<OrderedMealDTO>): Response<Unit>

    @POST("meals/{meal_id}/foods")
    suspend fun addFoodToMeal(@Path("meal_id") mealId: String, @Body foodDTO: AddFoodDTO): Response<AddFoodResponse>

    @PATCH("meals/{meal_id}/foods/{food_id}/weight")
    suspend fun editFoodWeight(@Path("meal_id") mealId: String, @Path("food_id") foodId: String, @Body weight: Double): Response<EditFoodWeightResponse>

    @DELETE("meals/{meal_id}/foods/{food_id}")
    suspend fun deleteFood(@Path("meal_id") mealId: String, @Path("food_id") foodId: String) : Response<Unit>

    @GET("meals/week")
    suspend fun getMealsByWeek(@Query("start_week_date") startWeekDate: String, @Query("end_week_date") endWeekDate: String): Response<MealsByWeekResponse>

    @GET("foods/")
    suspend fun getFoodsByPattern(@Query("pattern") pattern: String, @Query("skip") skip: Int, @Query("limit") limit: Int): Response<FoodListResponse>

}