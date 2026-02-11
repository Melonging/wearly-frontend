package ddwu.com.mobile.wearly_frontend.upload.data.model

data class SectionClothesData(
    val section: SectionInfoDto,
    val clothes: List<ClothesDto>,
    val total_count: Int
)

data class SectionInfoDto(
    val section_id: Long,
    val name: String,
    val closet_name: String
)

data class ClothesDto(
    val clothing_id: Long,
    val image: String?,
    val category: String?,
    val temperature: Int?,
    val created_at: String?
)
