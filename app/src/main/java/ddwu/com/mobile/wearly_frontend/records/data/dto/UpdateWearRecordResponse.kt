package ddwu.com.mobile.wearly_frontend.records.data.dto

data class UpdateWearRecordResponse(
    val success: Boolean,
    val data: UpdateWearRecordData?,
    val error: Any?
)

data class UpdateWearRecordData(
    val date_id: Long,
    val wear_date: String
)
