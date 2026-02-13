package ddwu.com.mobile.wearly_frontend.codidiary.data

class CodiClothesResponse (
    val success: Boolean,
    val data: ClothesListData
)

data class ClothesListData(
    val clothes: List<ClothItem>
)

data class ClothItem(
    val clothing_id: Int,
    val image: String,


    var isSelected: Boolean = false,
    var category_name: String? = ""
)