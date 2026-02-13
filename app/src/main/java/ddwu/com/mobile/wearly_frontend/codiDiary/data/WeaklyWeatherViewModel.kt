package ddwu.com.mobile.wearly_frontend.codiDiary.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ddwu.com.mobile.wearly_frontend.codiDiary.network.CodiCalendarRetrofitClient
import kotlinx.coroutines.launch

class WeaklyWeatherViewModel: ViewModel() {
    private val _weaklyWeatherData = MutableLiveData<List<WeaklyWeatherData>>()
    val weaklyWeatherData: LiveData<List<WeaklyWeatherData>> = _weaklyWeatherData

    /**
     * 주간 날씨 조회 API
     *
     * @param lat 위도
     * @param lon 경도
     * @param token 토큰
     */
    fun fetchWeeklyWeather(lat: Double, lon: Double, token: String) {
        viewModelScope.launch {
            try {
                val response = CodiCalendarRetrofitClient.weatherService.getWeeklyWeather("Bearer $token", lat, lon)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    val rawForecastData = body.data ?: emptyList()

                    _weaklyWeatherData.value = mapToWeaklyWeatherData(rawForecastData)
                } else {
                    Log.e("WeaklyWeatherVM", "실패 코드: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("WeaklyWeatherVM", "통신 에러: ${e.message}")
            }
        }
    }

    private fun mapToWeaklyWeatherData(forecast: List<ForecastItem>): List<WeaklyWeatherData> {
        return forecast.map { item ->
            val formattedDate = try {
                val parts = item.date.split("-")
                if (parts.size >= 3) {
                    val month = parts[1].toInt()
                    val day = parts[2].toInt()
                    "$month/$day"
                } else {
                    item.date
                }
            } catch (e: Exception) {
                item.date
            }

            WeaklyWeatherData(
                date = formattedDate,
                weatherIcon = getIconCode(item.weatherMain),
                temperature = "${item.tempMax}°/${item.tempMin}°"
            )
        }
    }

    private fun getIconCode(weatherMain: String): Int {
        return when (weatherMain.uppercase()) {
            "CLEAR" -> 0
            "CLOUDS", "PARTLY_CLOUDY", "CLOUDY" -> 1
            "RAIN", "RAINY" -> 2
            "SNOW", "SNOWY" -> 3
            else -> 0
        }
    }
}