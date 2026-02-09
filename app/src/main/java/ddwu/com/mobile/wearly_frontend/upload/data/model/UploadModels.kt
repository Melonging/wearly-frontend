package ddwu.com.mobile.wearly_frontend.upload.data.model

data class UploadImageData(
    val imageUrl: String
)

data class ClothingResult(
    val clothing: ClothingInfo?
)

data class ClothingInfo(
    val clothing_id: Int,
    val image: String,
    val category: String?,
    val section_name: String?,
    val temperature: Int?
)

data class ClothingStatusData(
    val jobId: String,
    val status: String,          // "processing" | "completed" | "failed"
    val result: ClothingResult? = null,
    val error: String? = null
)
