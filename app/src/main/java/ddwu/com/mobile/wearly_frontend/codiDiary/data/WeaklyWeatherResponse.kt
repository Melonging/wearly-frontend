package ddwu.com.mobile.wearly_frontend.codiDiary.data

data class WeaklyWeatherResponse(
    val success: Boolean,
    val data: WeatherForecastData?,
    val error: WeatherError?,
    val timestamp: String
)

// data 내부 구조
data class WeatherForecastData(
    val location: LocationData,
    val forecast: List<ForecastItem>,
    val forecastTime: String
)

// location 정보
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val address: String
)

// 일별 예보 데이터
data class ForecastItem(
    val date: String,
    val dayOfWeek: String,
    val temperature: Temperature,
    val precipitation: Precipitation? = null, // Mock 데이터 파싱을 위해 널 허용 추가
    val humidity: Int? = null,
    val sky: Sky
)

// 온도 데이터
data class Temperature(
    val min: Double,
    val max: Double
)

// 강수 데이터
data class Precipitation(
    val probability: Int,
    val amount: String
)

// 날씨 상태 (아이콘 결정용)
data class Sky(
    val code: String,
    val description: String? = null
)

// 에러 발생 시 구조
data class WeatherError(
    val code: String,
    val message: String,
    val details: Any?
)