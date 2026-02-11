package ddwu.com.mobile.wearly_frontend.codiDiary.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import ddwu.com.mobile.wearly_frontend.codiDiary.network.CodiCalendarRetrofitClient
import kotlinx.coroutines.launch

class WeaklyWeatherViewModel: ViewModel() {
    private val _weaklyWeatherData = MutableLiveData<List<WeaklyWeatherData>>()
    val weaklyWeatherData: LiveData<List<WeaklyWeatherData>> = _weaklyWeatherData

    /**
     * 주간 날씨 API 호출 함수.
     *
     * @param lat 위도
     * @param lon 경도
     * @param token api 토큰
     */
    fun fetchWeeklyWeather(lat: Double, lon: Double, token: String) {
        viewModelScope.launch {
            try {
                val response = CodiCalendarRetrofitClient.weatherService.getWeeklyWeather("Bearer $token", lat, lon)
                if (response.isSuccessful && response.body() != null) {
                    val rawForcastData = response.body()!!.data?.forecast ?: emptyList()

                    _weaklyWeatherData.value = mapToWeaklyWeatherData(rawForcastData)
                } else {
                    Log.e("WeaklyWeatherVM", "실패 코드: ${response.code()}")
                    setMockData()
                }
            } catch (e: Exception) {
                Log.e("WeaklyWeatherVM", "통신 에러: ${e.message}")
                setMockData()
            }
        }
    }

    /**
     * 테스트용 데이터 세팅 함수
     */
    private fun setMockData() {
        val jsonMockData = """
        {
          "data": {
            "forecast": [
              { "date": "2026-01-10", "sky": { "code": "CLOUDY" }, "temperature": { "min": -2.0, "max": 5.0 } },
              { "date": "2026-01-11", "sky": { "code": "RAINY" }, "temperature": { "min": -3.0, "max": 4.0 } },
              { "date": "2026-01-12", "sky": { "code": "PARTLY_CLOUDY" }, "temperature": { "min": -1.0, "max": 6.0 } },
              { "date": "2026-01-13", "sky": { "code": "CLEAR" }, "temperature": { "min": 0.0, "max": 7.0 } },
              { "date": "2026-01-14", "sky": { "code": "PARTLY_CLOUDY" }, "temperature": { "min": -2.0, "max": 5.0 } },
              { "date": "2026-01-15", "sky": { "code": "CLOUDY" }, "temperature": { "min": -1.0, "max": 6.0 } },
              { "date": "2026-01-16", "sky": { "code": "CLEAR" }, "temperature": { "min": 1.0, "max": 8.0 } }
            ]
          }
        }
        """.trimIndent()

        val gson = Gson()
        val mockResponse = gson.fromJson(jsonMockData, WeaklyWeatherResponse::class.java)
        val mockForecast = mockResponse.data?.forecast ?: emptyList()

        _weaklyWeatherData.value = mapToWeaklyWeatherData(mockForecast)
    }

    /**
     * forcastData를 WeaklyWeatherData 형식으로 변환하는 함수
     *
     * @return 변환된 WeaklyWeatherData
     */
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
                weatherIcon = getIconCode(item.sky.code),
                temperature = "${item.temperature.max.toInt()}°/${item.temperature.min.toInt()}°"
            )
        }
    }

    /**
     * 응답 날씨 문자열을 코드로 변환하는 함수
     *
     * @param weatherCode 응답 날씨 문자열
     * @return 변환된 코드
     */
    private fun getIconCode(weatherCode: String): Int {
        return when (weatherCode) {
            "CLEAR" -> 0
            "PARTLY_CLOUDY", "CLOUDY" -> 1
            "RAINY" -> 2
            "SNOWY" -> 3
            else -> 0
        }
    }
}