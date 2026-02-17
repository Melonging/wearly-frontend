package ddwu.com.mobile.wearly_frontend.codidiary.data

typealias WeaklyWeatherResponse = ApiResponse<List<ForecastItem>>

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
    val details: Any? = null
)