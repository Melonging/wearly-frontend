package ddwu.com.mobile.wearly_frontend.codiDiary.data

data class CodiDiaryEditRequest (
    val outfit_name: String? = null,
    val memo: String? = null,
    val is_heart: Boolean? = null,
    val wear_date: String? = null,
    val temp_min: Double? = null,
    val temp_max: Double? = null,
    val weather_icon: String? = null
)