package ddwu.com.mobile.wearly_frontend.codiDiary.network

import ddwu.com.mobile.wearly_frontend.codiDiary.data.WeaklyWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WeaklyWeatherService {
    @GET("api/weather/weekly")
    suspend fun getWeeklyWeather(
        @Header("Authorization") token: String,
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double
    ): retrofit2.Response<WeaklyWeatherResponse>
}