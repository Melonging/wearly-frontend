package ddwu.com.mobile.wearly_frontend.upload.data.model.closet

data class ClothingUpdateResponseDto(
    val success: Boolean,
    val message: String?,
    val data: ClothingUpdateDataDto?,
    val error: Any?
)

data class ClothingUpdateDataDto(
    val clothing_id: Int,
    val message: String?
)