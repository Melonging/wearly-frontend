package ddwu.com.mobile.wearly_frontend.upload.data.util

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object MultipartUtil {
    fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part {
        val cr = context.contentResolver
        val mime = cr.getType(uri) ?: "image/*"
        val bytes = cr.openInputStream(uri)?.use { it.readBytes() }
            ?: error("Cannot open uri")

        val body = bytes.toRequestBody(mime.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", "photo.jpg", body)
    }

    fun textPart(value: String): RequestBody =
        value.toRequestBody("text/plain".toMediaTypeOrNull())
}
