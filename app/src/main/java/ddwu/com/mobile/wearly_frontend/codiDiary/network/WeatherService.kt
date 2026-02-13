package ddwu.com.mobile.wearly_frontend.codiDiary.network

import ddwu.com.mobile.wearly_frontend.codiDiary.data.PastWeatherResponse
import ddwu.com.mobile.wearly_frontend.codiDiary.data.WeaklyWeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WeatherService {
    // 주간 날씨
    @GET("api/weather/weekly")
    suspend fun getWeeklyWeather(
        @Header("Authorization") token: String,
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double
    ): Response<WeaklyWeatherResponse>

    // 특정일 날씨
    @GET("api/weather/historical")
    suspend fun getPastWeather(
        @Header("Authorization") token: String,
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("date") date: String
    ): Response<PastWeatherResponse>
}