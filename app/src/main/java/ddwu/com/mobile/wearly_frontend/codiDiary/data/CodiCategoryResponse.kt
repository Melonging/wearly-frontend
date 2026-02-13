package ddwu.com.mobile.wearly_frontend.codiDiary.data

data class CodiCategoryResponse(
    val success: Boolean,
    val data: CategoryListData
)

data class CategoryListData(
    val categories: List<CategoryItem>
)

data class CategoryItem(
    val category_id: Int,
    val name: String
)