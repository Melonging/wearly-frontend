package ddwu.com.mobile.wearly_frontend.codiDiary.data
data class CodiDiaryViewResponse(
    val success: Boolean,
    val data: List<WearRecordDetail>?,
    val error: String?
)

data class WearRecordDetail(
    val date_id: Int,
    val wear_date: String,
    val memo: String?,
    val weather: WeatherDetail?,
    val outfit: OutfitDetail?
)

data class WeatherDetail(
    val temp_min: Double,
    val temp_max: Double,
    val weather_icon: String
)

data class OutfitDetail(
    val outfit_id: Int,
    val outfit_name: String,
    val is_heart: Boolean,
    val clothes: List<ClothingDetail>?
)

data class ClothingDetail(
    val clothing_id: Int,
    val image: String,
    val category_name: String,
    val layout: LayoutDetail
)

data class LayoutDetail(
    val x_ratio: Double,
    val y_ratio: Double,
    val z_index: Int
)