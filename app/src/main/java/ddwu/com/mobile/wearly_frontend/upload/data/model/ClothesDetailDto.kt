package ddwu.com.mobile.wearly_frontend.upload.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClothesDetailDto(
    val clothing_id: Long,
    val temperature: Int?,
    val image: String?,
    val category_id: Long?,
    val section_id: Long?
) : Parcelable