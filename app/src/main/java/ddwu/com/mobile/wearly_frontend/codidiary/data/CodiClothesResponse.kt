package ddwu.com.mobile.wearly_frontend.codidiary.data

class CodiClothesResponse (
    val success: Boolean,
    val data: ClothesListData
)

data class ClothesListData(
    val clothes: List<DiaryClothItem>
)

data class DiaryClothItem(
    val clothing_id: Int,
    val image: String,
    val category_name: String? = null,
    val layout: DiaryLayout? = null,

    var isSelected: Boolean = false
)


data class DiaryLayout(
    val x_ratio: Double,
    val y_ratio: Double,
    val z_index: Int
)