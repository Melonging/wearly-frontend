package ddwu.com.mobile.wearly_frontend.upload.data.model.closet

data class ClosetListResponseDto(
    val success: Boolean,
    val data: List<ClosetDto>?
)

data class ClosetDto(
    val closet_id: Int,
    val closet_name: String
)