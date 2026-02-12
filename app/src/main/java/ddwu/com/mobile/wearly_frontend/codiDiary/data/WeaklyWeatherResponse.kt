package ddwu.com.mobile.wearly_frontend.codiDiary.data

data class WeaklyWeatherResponse(
    val success: Boolean,
    val data: List<ForecastItem>?,
    val error: WeatherError?,
    val timestamp: String
)

data class ForecastItem(
    val date: String,
    val dayOfWeek: String,
    val temperature: Int,
    val tempMin: Int,
    val tempMax: Int,
    val weatherMain: String,
    val weatherDescription: String,
    val weatherIcon: String
)

data class WeatherError(
    val code: String,
    val message: String,
    val details: Any?
)