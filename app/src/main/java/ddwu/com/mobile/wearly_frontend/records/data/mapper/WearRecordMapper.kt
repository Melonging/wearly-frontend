package ddwu.com.mobile.wearly_frontend.records.data.mapper

import ddwu.com.mobile.wearly_frontend.records.data.dto.WearRecordDto
import ddwu.com.mobile.wearly_frontend.records.data.model.WearRecordItemUi

fun WearRecordDto.toUi(): WearRecordItemUi {

    val title = outfit?.outfit_name?.takeIf { it.isNotBlank() }
        ?: if (outfit != null) "코디" else "갤러리 기록"

    val tempText = if (weather?.temp_min != null && weather.temp_max != null) {
        "${stripZero(weather.temp_min)}°/${stripZero(weather.temp_max)}°"
    } else null

    return WearRecordItemUi(
        id = date_id,
        title = title,
        dateText = wear_date,
        tempText = tempText,
        iconCode = weather?.weather_icon,
        thumbUrl = image_url,
        isHeart = is_heart,
        isOutfit = outfit != null
    )
}

private fun stripZero(v: Double): String =
    if (v % 1.0 == 0.0) v.toInt().toString() else v.toString()
