package ddwu.com.mobile.wearly_frontend.closet.data

import com.google.gson.annotations.SerializedName

//공통
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: String?
)

data class ClosetItem(
    @SerializedName("closet_id")
    val closetId: Int,
    @SerializedName("closet_name")
    val closetName: String
)


data class ClosetData(
    @SerializedName("closet_type")
    val closetType: String,
    val sections: List<SectionItem>
)

data class SectionItem(
    @SerializedName("section_name")
    val sectionName: String,
    @SerializedName("clothes_count")
    val clothesCount: Int
)