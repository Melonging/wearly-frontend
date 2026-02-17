package ddwu.com.mobile.wearly_frontend.codidiary.data

import ddwu.com.mobile.wearly_frontend.codidiary.data.ApiResponse

data class CodiDiaryRecordRequest(
    val wear_date: String,

    val clothes_ids: List<Int>? = null,
    val image_url: String? = null,

    val outfit_name: String,

    val memo: String? = null,
    val is_heart: Boolean = false,

    val temp_min: Double? = null,
    val temp_max: Double? = null,
    val weather_icon: String? = null
)

typealias CodiDiaryRecordResponse = ApiResponse<CodiDiaryRecordResult>

data class CodiDiaryRecordResult(
    val date_id: Int,
    val outfit_id: Int,
    val image_url: String? = null
)