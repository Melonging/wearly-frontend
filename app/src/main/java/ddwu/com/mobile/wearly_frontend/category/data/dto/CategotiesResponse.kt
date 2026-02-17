package ddwu.com.mobile.wearly_frontend.category.data.dto

data class CategoriesResponse(
    val success: Boolean,
    val data: CategoriesData? = null,
    val error: ApiError? = null
)

data class CategoriesData(
    val categories: List<CategoryDto>
)

data class CategoryDto(
    val category_id: Long,
    val name: String
)

data class ClothesResponse(
    val success: Boolean,
    val data: ClothesData? = null,
    val error: ApiError? = null
)

data class ClothesData(
    val clothes: List<ClothingThumbDto>
)

data class ClothingThumbDto(
    val clothing_id: Long,
    val image: String
)

data class ApiError(
    val code: String,
    val message: String,
    val field: String? = null
)