package es.upm.macroscore.data.network

import es.upm.macroscore.data.network.dto.meals.MealDTO
import es.upm.macroscore.data.network.dto.signup.SignUpDTO
import es.upm.macroscore.data.network.response.login.LogInResponse
import es.upm.macroscore.data.network.response.signup.CheckEmailResponse
import es.upm.macroscore.data.network.response.meals.MealByDateResponse
import es.upm.macroscore.data.network.response.signup.CheckUsernameResponse
import es.upm.macroscore.data.network.response.signup.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
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

    @POST("refresh")
    suspend fun refreshToken(@Body refreshToken: String): Response<LogInResponse>

    @GET("meals")
    suspend fun getMealsByDate(@Query("target_date") targetDate: String): Response<List<MealByDateResponse>>

    @POST("meals")
    suspend fun addFood(@Body mealDTO: MealDTO): Response<MealByDateResponse>
}