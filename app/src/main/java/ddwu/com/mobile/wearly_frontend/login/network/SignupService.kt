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

interface SignupService {

    // 로그인
    @POST("/api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // 회원가입
    @POST("/api/auth/signup")
    fun signUp(@Body request: SignupRequest): Call<SignupResponse>
}