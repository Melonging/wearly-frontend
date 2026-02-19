package ddwu.com.mobile.wearly_frontend.codidiary.data

typealias PastWeatherResponse = ApiResponse<PastWeatherData>

data class PastWeatherData(
    val tempMin: Int,
    val tempMax: Int,
    val weatherIcon: String
)


// 01 = 맑음
// 02~04 = 구름
// 09~10 = 비
// 13 = 눈