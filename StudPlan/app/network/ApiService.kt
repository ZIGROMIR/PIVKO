package com.example.myapplication.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val code: Int, val msg: String, val token: String)
data class RegistrationRequest(val username: String, val password: String)

interface ApiService {
    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>
    @POST("register")
    suspend fun register(@Body body: RegistrationRequest): Response<Void>
}