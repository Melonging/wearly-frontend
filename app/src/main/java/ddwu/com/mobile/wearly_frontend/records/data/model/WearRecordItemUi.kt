package ddwu.com.mobile.wearly_frontend.records.data.model

data class WearRecordItemUi(
    val id: Long,
    val title: String,
    val dateText: String,
    val tempText: String?,
    val iconCode: String?,
    val thumbUrl: String?,
    val isHeart: Boolean,
    val isOutfit: Boolean
)
