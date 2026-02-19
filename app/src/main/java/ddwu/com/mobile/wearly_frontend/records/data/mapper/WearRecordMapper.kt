package ddwu.com.mobile.wearly_frontend.records.data.mapper

import ddwu.com.mobile.wearly_frontend.records.data.dto.WearRecordDto
import ddwu.com.mobile.wearly_frontend.records.data.model.WearRecordItemUi

fun WearRecordDto.toUi(): WearRecordItemUi {
    val min = this.weather?.temp_min
    val max = this.weather?.temp_max

    return WearRecordItemUi(
        id = this.date_id.toLong(),
        title = this.outfit?.outfit_name ?: "기록",
        dateText = this.wear_date,
        tempText = if (min != null && max != null) "${min.toInt()}°/${max.toInt()}°" else null,
        iconCode = this.weather?.weather_icon,
        thumbUrl = this.image_url ?: this.outfit?.clothes?.firstOrNull()?.image, // ✅ 핵심
        isHeart = this.is_heart,
        isOutfit = this.outfit != null
    )
}

private fun stripZero(v: Double): String =
    if (v % 1.0 == 0.0) v.toInt().toString() else v.toString()
