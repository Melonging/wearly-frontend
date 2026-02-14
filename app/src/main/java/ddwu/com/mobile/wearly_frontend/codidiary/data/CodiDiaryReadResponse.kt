package ddwu.com.mobile.wearly_frontend.codidiary.data

data class CodiDiaryReadResponse(
    val success: Boolean,
    val data: List<CodiDiaryRead>?,
    val error: String?
)

data class CodiDiaryRead(
    val idx: Int,
    val date_id: Int,
    val outfit_id: Int,
    val wear_date: String,
    val user_id: Int,
    val memo: String?,
    val temp_max: Double,
    val temp_min: Double,
    val weather_icon: String?,
    val image_url: String?,
    val is_heart: Boolean
)