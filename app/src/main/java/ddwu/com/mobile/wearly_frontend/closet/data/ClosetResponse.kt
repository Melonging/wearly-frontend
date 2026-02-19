package ddwu.com.mobile.wearly_frontend.closet.data

import com.google.gson.annotations.SerializedName

//공통
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T?,
    val error: String?
)

data class ClosetItem(
    @SerializedName("closet_id")
    val closetId: Int,
    @SerializedName("closet_name")
    val closetName: String,
    @SerializedName("closet_template_id")
    val closetTemplateId: Int? = null,
    val message: String? = null,
    var isSelected: Boolean = false
)

data class AddClosetRequest(
    val closetTemplateId: Int,
    val closetName: String
)

data class ClosetViewData(
    val closet: ClosetDetail,
    val sections: List<SectionDetail>,
    @SerializedName("total_sections")
    val totalSections: Int
)

data class ClosetDetail(
    @SerializedName("closet_id")
    val closetId: Int,
    @SerializedName("closet_name")
    val closetName: String,
    @SerializedName("closet_type")
    val closetType: String,
    @SerializedName("closet_template_id")
    val closetTemplateId: Int
)
data class SectionDetail(
    @SerializedName("section_id")
    val sectionId: Int,
    @SerializedName("section_name")
    val sectionName: String,
    @SerializedName("section_type")
    val sectionType: String,
    @SerializedName("clothing_count")
    val clothingCount: Int
)

//섹션 속 옷 조회 데이터 구조
data class SectionClothesData(
    val section: SectionInfo,
    val clothes: List<ClothingItem>,
    @SerializedName("total_count")
    val totalCount: Int
)

//섹션 간략 정보
data class SectionInfo(
    @SerializedName("section_id")
    val sectionId: Int,
    val name: String,
    @SerializedName("closet_name")
    val closetName: String
)

//개별 옷 아이템 정보
data class ClothingItem(
    @SerializedName("clothing_id")
    val clothingId: Int,
    val image: String,
    val category: String,
    val temperature: Int,
    @SerializedName("created_at")
    val createdAt: String
)

// 옷 상세 정보 데이터 구조
data class ClothingDetail(
    @SerializedName("clothing_id")
    val clothingId: Int,
    val temperature: Int,
    val image: String,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("section_id")
    val sectionId: Int
)

data class UpdateClosetNameRequest(
    val closetName: String,
    val templateId: Int
)

data class DeleteClosetData(
    @SerializedName("closet_id")
    val closetId: Int
)

data class CategoryData(
    val categories: List<CategoryItem>
)

data class CategoryItem(
    @SerializedName("category_id")
    val categoryId: Int,
    val name: String
)

data class CreateClosetRequest(
    val closetTemplateId: Int,
    val closetName: String
)
