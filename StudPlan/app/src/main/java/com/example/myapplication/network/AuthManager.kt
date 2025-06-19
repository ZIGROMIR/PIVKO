package com.example.myapplication.network

import android.content.Context
import android.util.Log
import network.ApiClient
import network.LoginRequest
import retrofit2.HttpException
import androidx.core.content.edit

data class LoginRequest(
    val username: String,
    val password: String
)

suspend fun loginAndStoreToken(
    username: String,
    password: String,
    context: Context
): Boolean {
    return try {
        val resp = ApiClient.apiService.login(LoginRequest(username, password))
        if (resp.isSuccessful) {
            resp.body()?.token?.let { token ->
                context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                    .edit { putString("token", token) }
                Log.d("Auth", "Token saved")
                return true
            }
        }
        Log.e("Auth", "Login failed: ${resp.code()}")
        false
    } catch (e: Exception) {
        Log.e("Auth", "Network error on login", e)
        false
    }
}

suspend fun registerUser(
    username: String,
    password: String,
    context: Context
): Boolean {
    val student = StudentRequest(
        ticketNo   = username,
        firstName  = username,
        patronymic = "",
        lastName   = "",
        city       = "",
        school     = "",
        college    = "",
        className  = "",
        groupName  = "",
        telephone  = ""
    )

    return try {
        val resp = ApiClient.apiService.registerStudent(student)
        if (resp.isSuccessful) {
            Log.d("Register", "Student registered!")
            true
        } else {
            Log.e("Register", "Server error: ${resp.code()} ${resp.errorBody()?.string()}")
            false
        }
    } catch (e: HttpException) {
        Log.e("Register", "HttpException on register", e)
        false
    } catch (e: Exception) {
        Log.e("Register", "Network error on register", e)
        false
    }
}