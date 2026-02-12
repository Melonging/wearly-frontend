package ddwu.com.mobile.wearly_frontend.upload.data.remote

import ddwu.com.mobile.wearly_frontend.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.TEST_TOKEN}")
            .build()

        return chain.proceed(request)
    }
}
