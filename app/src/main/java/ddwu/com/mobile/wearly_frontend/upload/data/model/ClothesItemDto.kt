package ddwu.com.mobile.wearly_frontend.upload.data.model

import com.google.gson.annotations.SerializedName

// 서버 응답용
data class ClothesItemDto(
    @SerializedName("clothing_id") val clothingId: Long,
    @SerializedName("image") val image: String?
)