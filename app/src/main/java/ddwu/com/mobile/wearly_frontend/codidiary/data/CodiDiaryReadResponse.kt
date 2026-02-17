package ddwu.com.mobile.wearly_frontend.codidiary.data

typealias CodiDiaryReadResponse = ApiResponse<List<CodiDiaryRead>>

data class CodiDiaryRead(
    val date_id: Int,
    val wear_date: String,
    val memo: String? = null,
    val is_heart: Boolean,
    val image_url: String? = null,
    val weather: DiaryWeather? = null,
    val outfit: DiaryOutfit? = null
)

data class DiaryWeather(
    val temp_min: Double? = null,
    val temp_max: Double? = null,
    val weather_icon: String? = null
)

data class DiaryOutfit(
    val outfit_id: Int,
    val outfit_name: String,
    val clothes: List<DiaryClothItem> = emptyList()
)