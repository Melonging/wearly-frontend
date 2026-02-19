package ddwu.com.mobile.wearly_frontend.upload.data.remote

import android.content.Context
import ddwu.com.mobile.wearly_frontend.BuildConfig
import ddwu.com.mobile.wearly_frontend.TokenManager
import ddwu.com.mobile.wearly_frontend.upload.data.remote.closet.ClosetApi
import ddwu.com.mobile.wearly_frontend.upload.data.remote.upload.UploadApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = BuildConfig.BASE_URL
    private var retrofit: Retrofit? = null

    fun getRetrofit(context: Context): Retrofit {
        return retrofit ?: synchronized(this) {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val authInterceptor = Interceptor { chain ->
                val tokenManager = TokenManager(context)
                val token = tokenManager.getToken() ?: ""

                val req = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${token.trim()}")
                    .build()
                chain.proceed(req)
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(authInterceptor)
                .addInterceptor(logger)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .also { retrofit = it }
        }
    }
    fun uploadApi(context: Context): UploadApi = getRetrofit(context).create(UploadApi::class.java)
    fun closetApi(context: Context): ClosetApi = getRetrofit(context).create(ClosetApi::class.java)
}