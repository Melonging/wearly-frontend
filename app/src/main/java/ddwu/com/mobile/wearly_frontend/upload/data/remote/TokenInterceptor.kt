package ddwu.com.mobile.wearly_frontend.upload.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenProvider()
        val req = chain.request().newBuilder().apply {
            if (!token.isNullOrBlank()) {
                header("Authorization", "Bearer $token")
            }
        }.build()
        return chain.proceed(req)
    }
}
