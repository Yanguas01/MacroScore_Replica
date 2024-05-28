package es.upm.macroscore.data.network

import es.upm.macroscore.data.network.request.signup.SignUpRequestEntity
import es.upm.macroscore.data.network.response.login.LogInResponse
import es.upm.macroscore.data.network.response.signup.CheckEmailResponse
import es.upm.macroscore.data.network.response.mealByDate.MealByDateResponse
import es.upm.macroscore.data.network.response.signup.CheckUsernameResponse
import es.upm.macroscore.data.network.response.signup.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MacroScoreApiService {

    @GET("users/check_email")
    suspend fun checkEmail(@Query("email") email: String): Response<CheckEmailResponse>

    @GET("users/check_username")
    suspend fun checkUsername(@Query("username") username: String): Response<CheckUsernameResponse>

    @POST("signup")
    suspend fun createNewUser(@Body signUpRequestEntity: SignUpRequestEntity): Response<SignUpResponse>

    @POST("login")
    suspend fun logUser(@Field("username") username: String, @Field("password") password: String, @Field("scope") scope: List<String>): Response<LogInResponse>

    @POST("refresh")
    suspend fun refreshToken(@Body refreshToken: String): Response<LogInResponse>

    @GET("meals/{target_date}")
    suspend fun getMealsByDate(@Path("target_date") targetDate: String, @Header("Authorization") token: String): List<MealByDateResponse>


}