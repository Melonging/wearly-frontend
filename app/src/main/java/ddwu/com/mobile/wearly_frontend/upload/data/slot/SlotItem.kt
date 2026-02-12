package ddwu.com.mobile.wearly_frontend.upload.data.slot

import android.net.Uri

sealed class SlotItem {
    object Empty: SlotItem()
    data class Image(
        val id: Long? = null,        //  상세 이동용
        val imageUrl: String? = null, //  서버용 URL
        val uri: Uri? = null,        //  로컬 카메라용
        val resId: Int? = null
    ) : SlotItem()
}