package ddwu.com.mobile.wearly_frontend.upload.data.remote

import ddwu.com.mobile.wearly_frontend.BuildConfig
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
    private val TEST_TOKEN: String = BuildConfig.TEST_TOKEN

    private val retrofit: Retrofit by lazy {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val token = TEST_TOKEN.trim()
            val req = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(req)
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
}
