package ddwu.com.mobile.wearly_frontend.codiDiary.data

data class WeaklyWeatherData(
    val date: String,
    val weatherIcon: Int = 0,
    // 0: 맑음
    // 1: 흐림
    // 2: 비
    // 3: 눈
    val temperature: String
)
