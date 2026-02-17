package ddwu.com.mobile.wearly_frontend.records.data.dto

data class WearRecordResponse(
    val success: Boolean,
    val data: List<WearRecordDto>?,
    val error: Any?
)

data class WearRecordDto(
    val date_id: Long,
    val wear_date: String,
    val memo: String?,
    val is_heart: Boolean,
    val image_url: String?,
    val weather: WeatherDto?,
    val outfit: OutfitDto?
)

data class WeatherDto(
    val temp_min: Double?,
    val temp_max: Double?,
    val weather_icon: String?
)

data class OutfitDto(
    val outfit_id: Long,
    val outfit_name: String?,
    val clothes: List<ClothDto>?
)

data class ClothDto(
    val clothing_id: Long,
    val image: String?,
    val category_name: String?,
    val layout: LayoutDto?
)

data class LayoutDto(
    val x_ratio: Double?,
    val y_ratio: Double?,
    val z_index: Int?
)

