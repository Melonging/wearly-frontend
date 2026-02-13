package ddwu.com.mobile.wearly_frontend.codiDiary.data.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ddwu.com.mobile.wearly_frontend.codiDiary.data.ForecastItem
import ddwu.com.mobile.wearly_frontend.codiDiary.data.WeaklyWeatherData
import ddwu.com.mobile.wearly_frontend.codiDiary.network.CodiCalendarRetrofitClient
import kotlinx.coroutines.launch

class WeatherViewModel: ViewModel() {
    private val _weaklyWeatherData = MutableLiveData<List<WeaklyWeatherData>>()
    val weaklyWeatherData: LiveData<List<WeaklyWeatherData>> = _weaklyWeatherData

    private val _pastWeatherData = MutableLiveData<WeaklyWeatherData?>()
    val pastWeatherData: LiveData<WeaklyWeatherData?> = _pastWeatherData



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
                    Log.e("WeatherVM", "실패 코드: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("WeatherVM", "통신 에러: ${e.message}")
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
                temperature = "${item.tempMin}°/${item.tempMax}°"
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




    /**
     * 과거 날씨 조회 API
     *
     * @param lat 위도
     * @param lon 경도
     * @param token 토큰
     */
    fun fetchPastWeather(lat: Double, lon: Double, date: String, token: String) {
        viewModelScope.launch {
            Log.d("WeatherVM", "API 호출 시작 - 날짜: $date, 위도: $lat, 경도: $lon")

            try {
                val response = CodiCalendarRetrofitClient.weatherService.getPastWeather("Bearer $token", lat, lon, date)

                // 1. 응답이 오긴 했는지 확인
                Log.d("WeatherVM", "응답 코드: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Log.d("WeatherVM", "서버 응답 바디: $body") // 바디 내용 확인

                    if (body.success) {
                        body.data?.let { data ->
                            _pastWeatherData.value = WeaklyWeatherData(
                                date = date,
                                weatherIcon = convertIconToCode(data.weatherIcon),
                                temperature = "${data.tempMin}°/${data.tempMax}°"
                            )
                            Log.d("WeatherVM", "LiveData 업데이트 완료: ${_pastWeatherData.value}")
                        }
                    } else {
                        Log.e("WeatherVM", "성공은 했으나 success 필드가 false임")
                    }
                } else {
                    // 2. 응답은 왔으나 에러인 경우 (400, 401, 404, 500 등)
                    val errorBody = response.errorBody()?.string()
                    Log.e("WeatherVM", "API 실패 상세: $errorBody")
                }
            } catch (e: Exception) {
                // 3. 아예 통신 자체가 실패한 경우 (인터넷 연결, URL 오류 등)
                Log.e("WeatherVM", "통신 예외 발생: ${e.message}")
                e.printStackTrace() // 스택 트레이스 출력해서 어디서 터졌는지 확인
            }
        }
    }

    private fun convertIconToCode(icon: String): Int {
        return when {
            icon.startsWith("01") -> 0
            icon.startsWith("02") || icon.startsWith("03") || icon.startsWith("04") -> 1
            icon.startsWith("09") || icon.startsWith("10") -> 2
            icon.startsWith("13") -> 3
            else -> 0
        }
    }
}