package ddwu.com.mobile.wearly_frontend.upload.data.model.closet

data class ClothingDeleteResponseDto(
    val success: Boolean,
    val message: String?,
    val data: ClothingDeleteDataDto?,
    val error: Any?
)

data class ClothingDeleteDataDto(
    val clothing_id: Int,
    val message: String?
)