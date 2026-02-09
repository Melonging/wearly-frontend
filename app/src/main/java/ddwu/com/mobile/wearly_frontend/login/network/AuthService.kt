package ddwu.com.mobile.wearly_frontend.login.network

import ddwu.com.mobile.wearly_frontend.login.data.AuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/auth/send-code")
    fun sendEmailCode(
        @Body request: Map<String, String>
    ): Call<AuthResponse>

    @POST("/auth/verify-code")
    fun verifyEmailCode(
        @Body request: Map<String, String>
    ): Call<AuthResponse>
}
