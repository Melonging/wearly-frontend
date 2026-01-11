package ddwu.com.mobile.wearly_frontend.upload.data

sealed class SlotItem {
    object Empty: SlotItem()
    data class Image(val path: String): SlotItem()
}