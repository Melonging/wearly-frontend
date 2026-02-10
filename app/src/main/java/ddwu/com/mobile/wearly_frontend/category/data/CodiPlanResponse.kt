package ddwu.com.mobile.wearly_frontend.category.data

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val success: Boolean,
    val data: CategoryData
)

data class CategoryData(
    val categories: List<CategoryDto>
)

data class CategoryDto(
    @SerializedName("category_id")
    val categoryId: Int,

    @SerializedName("name")
    val name: String
)