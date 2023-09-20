package service

import dto.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("register")
    fun registerUser(@Body user: User): Call<Void>
    @POST("login")
    fun loginUser(@Body user: User): Call<Void>

    @POST("work-in")
    fun workIn(@Body user: User): Call<Map<String, Boolean>>
    @POST("work-out")
    fun workOut(@Body user: User): Call<Map<String, Boolean>>
    @POST("sync-status")
    fun syncStatus(@Body user: User): Call<Map<String, Boolean>>
}