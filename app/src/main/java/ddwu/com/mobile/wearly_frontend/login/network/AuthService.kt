package ddwu.com.mobile.wearly_frontend.login.network

import ddwu.com.mobile.wearly_frontend.login.data.AuthResponse
import ddwu.com.mobile.wearly_frontend.login.data.EmailRequest
import ddwu.com.mobile.wearly_frontend.login.data.LoginRequest
import ddwu.com.mobile.wearly_frontend.login.data.LoginResponse
import ddwu.com.mobile.wearly_frontend.login.data.SignupRequest
import ddwu.com.mobile.wearly_frontend.login.data.SignupResponse
import ddwu.com.mobile.wearly_frontend.login.data.VerifyRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    // 로그인 API endpoint
    @POST("api/auth/signin")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // 회원가입 API endpoint
    @POST("/api/auth/signup")
    fun signUp(@Body request: SignupRequest): Call<SignupResponse>

    // 인증번호 전송 API endpoint
    @POST("/auth/send-code")
    fun sendEmailCode(@Body request: EmailRequest): Call<AuthResponse>

    // 인증번호 확인 API endpoint
    @POST("/auth/verify-code")
    fun verifyEmailCode(@Body request: VerifyRequest): Call<AuthResponse>
}
