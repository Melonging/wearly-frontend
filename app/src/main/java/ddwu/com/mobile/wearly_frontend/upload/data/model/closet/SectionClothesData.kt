package ddwu.com.mobile.wearly_frontend.upload.data.model.closet

data class SectionClothesData(
    val section: SectionInfoDto,
    val clothes: List<ClothesDetailDto>,
    val total_count: Int
)

data class SectionInfoDto(
    val section_id: Long,
    val name: String,
    val closet_name: String
)
