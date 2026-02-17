package ddwu.com.mobile.wearly_frontend.codidiary.data

data class CodiDiaryEditRequest(
    val outfit_name: String? = null,
    val memo: String? = null,
    val is_heart: Boolean? = null,
    val wear_date: String? = null,
    val temp_min: Double? = null,
    val temp_max: Double? = null,
    val weather_icon: String? = null
)

typealias CodiDiaryEditResponse = ApiResponse<CodiDiaryUpdateResult>

data class CodiDiaryUpdateResult(
    val date_id: Int,
    val outfit_id: Int
)