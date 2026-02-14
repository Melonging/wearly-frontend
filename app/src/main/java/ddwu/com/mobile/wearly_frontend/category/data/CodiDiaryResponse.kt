package ddwu.com.mobile.wearly_frontend.category.data

data class ClothesResponse(
    val success: Boolean,
    val data: ClothesData
)

data class ClothesData(
    val clothes: List<ClothingItem>
)

data class ClothingItem(
    val clothing_id: Int,
    val image: String
)

data class CategoryResponse(
    val success: Boolean,
    val data: CategoryData
)

//data class CategoryData(
//    val categories: List<CategoryItem>
//)

data class CategoryItem(
    val category_id: Int,
    val name: String
)