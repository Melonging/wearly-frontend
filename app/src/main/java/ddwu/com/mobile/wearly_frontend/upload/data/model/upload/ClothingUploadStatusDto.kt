package ddwu.com.mobile.wearly_frontend.upload.data.model.upload

data class ClothingUploadStatusDto(
    val jobId: String,
    val status: String,
    val currentStep: Int? = null,
    val stepName: String? = null,
    val progress: Int? = null,
    val result: ClothingUploadResultDto? = null,
    val error: String? = null
)
