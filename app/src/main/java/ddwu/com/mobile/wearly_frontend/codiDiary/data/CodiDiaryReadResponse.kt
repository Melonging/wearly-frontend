package ddwu.com.mobile.wearly_frontend.codiDiary.data

data class CodiDiaryReadResponse(
    val success: Boolean,
    val data: List<CodiDiaryRead>?,
    val error: String?
)

data class CodiDiaryRead(
    val date_id: Int,
    val wear_date: String,
    val memo: String?,
    val weather: CodiDiaryReadWeather?,
    val outfit: CodiDiaryReadOutfit?
)

data class CodiDiaryReadWeather(
    val temp_min: Double,
    val temp_max: Double,
    val weather_icon: String
)

data class CodiDiaryReadOutfit(
    val outfit_id: Int,
    val outfit_name: String,
    val is_heart: Boolean,
    val clothes: List<CodiDiaryReadCloth>?
)

data class CodiDiaryReadCloth(
    val clothing_id: Int,
    val image: String,
    val category_name: String,
    val layout: CodiDiaryReadLayout
)

data class CodiDiaryReadLayout(
    val x_ratio: Double,
    val y_ratio: Double,
    val z_index: Int
)