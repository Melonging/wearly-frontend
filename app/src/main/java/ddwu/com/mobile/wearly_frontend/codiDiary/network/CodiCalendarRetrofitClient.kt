package ddwu.com.mobile.wearly_frontend.codiDiary.network

import ddwu.com.mobile.wearly_frontend.codiDiary.data.CodiDiaryRecordRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CodiCalendarRetrofitClient {
    private const val BASE_URL = "https://wearly-backend-cvbo.onrender.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherService: WeaklyWeatherService by lazy {
        retrofit.create(WeaklyWeatherService::class.java)
    }

    val codiDiaryService: CodiDiaryService by lazy {
        retrofit.create(CodiDiaryService::class.java)
    }
}