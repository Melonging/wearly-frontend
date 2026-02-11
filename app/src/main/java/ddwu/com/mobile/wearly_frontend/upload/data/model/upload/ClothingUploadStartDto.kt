package ddwu.com.mobile.wearly_frontend.upload.data.model.upload

data class ClothingUploadStartDto(
    val jobId: String,
    val status: String,
    val currentStep: Int? = null,
    val stepName: String? = null
)
