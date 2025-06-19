package network

import com.example.myapplication.network.StudentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val code: Int, val msg: String, val token: String)

interface ApiService {
    @POST("student/info/add")
    suspend fun registerStudent(@Body student: StudentRequest): Response<Void>
    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>
}