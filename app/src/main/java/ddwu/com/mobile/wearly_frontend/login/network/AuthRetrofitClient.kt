import ddwu.com.mobile.wearly_frontend.login.network.AuthService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthRetrofitClient {
    private const val BASE_URL = "https://wearly-backend-cvbo.onrender.com/"
    // https://wearly-backend-cvbo.onrender.com

    val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}