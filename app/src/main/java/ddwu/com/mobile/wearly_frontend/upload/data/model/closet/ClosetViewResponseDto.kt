package ddwu.com.mobile.wearly_frontend.upload.data.model.closet

data class ClosetViewResponseDto(
    val success: Boolean,
    val data: ClosetViewDataDto?
)

data class ClosetViewDataDto(
    val closet: ClosetViewClosetDto,
    val sections: List<ClosetViewSectionDto>,
    val total_sections: Int
)

data class ClosetViewClosetDto(
    val closet_id: Int,
    val closet_name: String,
    val closet_type: String
)

data class ClosetViewSectionDto(
    val section_id: Int,
    val section_name: String,
    val section_type: String,
    val clothing_count: Int
)