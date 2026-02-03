package ddwu.com.mobile.wearly_frontend.upload.data

import android.net.Uri

sealed class SlotItem {
    object Empty: SlotItem()
    data class Image(
        val uri: Uri? = null,
        val resId: Int? = null
    ) : SlotItem()
}