package ddwu.com.mobile.wearly_frontend.upload.data.model.closet

data class CategoryResponse(
    val success: Boolean,
    val data: CategoryData?
)

data class CategoryData(
    val categories: List<Category>
)

data class Category(
    val category_id: Int,
    val name: String
)

