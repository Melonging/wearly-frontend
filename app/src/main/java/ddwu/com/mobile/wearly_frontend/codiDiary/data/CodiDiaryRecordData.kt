data class CodiDiaryRecordRequest(
    val wear_date: String,
    val clothes_ids: List<Int>,
    val outfit_name: String,
    val temp_min: Int,
    val temp_max: Int,
    val weather_icon: String,
    val memo: String,
    val is_heart: Boolean
)

data class CodiDiaryRecordResponse(
    val success: Boolean,
    val data: CodiDiaryResult?,
    val error: String?
)

data class CodiDiaryResult(
    val date_id: Int,
    val outfit_id: Int
)