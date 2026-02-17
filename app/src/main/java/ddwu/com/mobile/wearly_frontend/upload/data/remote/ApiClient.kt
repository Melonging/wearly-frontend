package ddwu.com.mobile.wearly_frontend.upload.data.remote

import ddwu.com.mobile.wearly_frontend.BuildConfig
import ddwu.com.mobile.wearly_frontend.TokenManager
import ddwu.com.mobile.wearly_frontend.records.data.remote.RecordsApi
import ddwu.com.mobile.wearly_frontend.upload.data.remote.closet.ClosetApi
import ddwu.com.mobile.wearly_frontend.upload.data.remote.upload.UploadApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val BASE_URL: String = BuildConfig.BASE_URL
    private val retrofit: Retrofit by lazy {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val token = TokenManager.getToken()

            val requestBuilder = chain.request().newBuilder()

            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }


        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(logger)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun uploadApi(): UploadApi = retrofit.create(UploadApi::class.java)
    fun closetApi(): ClosetApi = retrofit.create(ClosetApi::class.java)

    fun recordsApi(): RecordsApi = retrofit.create(RecordsApi::class.java)
}
