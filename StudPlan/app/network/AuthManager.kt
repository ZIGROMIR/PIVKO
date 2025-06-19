package com.example.myapplication.network

import android.content.Context
import android.util.Log
import retrofit2.HttpException

suspend fun loginAndStoreToken(
    username: String,
    password: String,
    context: Context
): Boolean {
    return try {
        val response = ApiClient.apiService.login(LoginRequest(username, password))
        if (response.isSuccessful) {
            val token = response.body()?.token
            if (!token.isNullOrEmpty()) {
                val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                prefs.edit().putString("token", token).apply()
                Log.d("Auth", "Успешный вход, токен сохранён")
                true
            } else {
                Log.e("Auth", "Пустой токен")
                false
            }
        } else {
            Log.e("Auth", "Ошибка входа: ${response.code()}")
            false
        }
    } catch (e: HttpException) {
        Log.e("Auth", "HttpException при входе", e)
        false
    } catch (e: Exception) {
        Log.e("Auth", "Ошибка сети при входе", e)
        false
    }
}

suspend fun registerUser(
    username: String,
    password: String,
    context: Context
): Boolean {
    return try {
        val response = ApiClient.apiService.register(RegistrationRequest(username, password))
        if (response.isSuccessful) {
            Log.d("Register", "Регистрация прошла успешно")
            true
        } else {
            Log.e("Register", "Ошибка регистрации: ${response.code()}")
            false
        }
    } catch (e: HttpException) {
        Log.e("Register", "HttpException при регистрации", e)
        false
    } catch (e: Exception) {
        Log.e("Register", "Ошибка сети при регистрации", e)
        false
    }
}