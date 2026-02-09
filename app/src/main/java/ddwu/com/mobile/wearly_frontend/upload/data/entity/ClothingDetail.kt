package ddwu.com.mobile.wearly_frontend.upload.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class ClothingDetail(
    val uri: String? = null,
    val resId: Int? = null,
    val category: String,
    val recommendedTemp: Int,
    val location: String
) : Parcelable

/*
data class ClothesDetailDto(
    val clothes_image: String?,
    val weather: String?,
    val color: String?
)
 */

