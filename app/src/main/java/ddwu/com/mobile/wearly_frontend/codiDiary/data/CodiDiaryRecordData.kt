package ddwu.com.mobile.wearly_frontend.codiDiary.data

data class CodiDiaryRecordRequest(
    val wear_date: String,
    val clothes_ids: List<Int>,
    val outfit_name: String,
    val latitude: Double,
    val longitude: Double,
    val memo: String,
    val is_heart: Boolean
)

// 저장 성공 응답 모델
data class CodiDiaryRecordResponse(
    val success: Boolean,
    val data: SaveData?,
    val error: String?
)

data class SaveData(
    val date_id: Int,
    val outfit_id: Int
)
