package es.upm.macroscore.data.network

import es.upm.macroscore.data.network.request.signup.SignUpRequest
import es.upm.macroscore.data.network.response.mealByDate.MealByDateResponse
import es.upm.macroscore.data.network.response.signup.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface MacroScoreApiService {

    @POST("/signup")
    suspend fun createNewUser(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @GET("/meals/{target_date}")
    suspend fun getMealsByDate(@Path("target_date") targetDate: String, @Header("Authorization") token: String): List<MealByDateResponse>
}