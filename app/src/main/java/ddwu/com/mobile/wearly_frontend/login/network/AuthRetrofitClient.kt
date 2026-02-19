import ddwu.com.mobile.wearly_frontend.login.network.AuthService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthRetrofitClient {
    private const val BASE_URL = "https://wearly-backend-cvbo.onrender.com/"
    // https://wearly-backend-cvbo.onrender.com

    private val okHttp by lazy {
        okhttp3.OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .callTimeout(40, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val req = chain.request()
                android.util.Log.d("HTTP", " ${req.method} ${req.url}")
                val res = chain.proceed(req)
                android.util.Log.d("HTTP", " ${res.code} ${req.url}")
                res
            }
            .build()
    }

    val authService: AuthService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}