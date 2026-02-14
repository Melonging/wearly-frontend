package ddwu.com.mobile.wearly_frontend.codidiary.data

data class CodiCategoryResponse(
    val success: Boolean,
    val data: CategoryListData
)

data class CategoryListData(
    val categories: List<DiaryCategoryItem>
)

data class DiaryCategoryItem(
    val category_id: Int,
    val name: String,
)