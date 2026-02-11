package ddwu.com.mobile.wearly_frontend.upload.data.repository

import android.content.Context
import android.net.Uri
import ddwu.com.mobile.wearly_frontend.upload.data.model.upload.ClothingUploadStartDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.upload.ClothingUploadStatusDto
import ddwu.com.mobile.wearly_frontend.upload.data.remote.upload.UploadApi
import ddwu.com.mobile.wearly_frontend.upload.data.util.MultipartUtil
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadRepository(private val api: UploadApi) {

    suspend fun startClothingUpload(
        imagePart: MultipartBody.Part,
        sectionId: RequestBody
    ): ClothingUploadStartDto {
        val res = api.startClothingUpload(imagePart, sectionId)
        if (!res.success || res.data == null) {
            throw RuntimeException(res.error?.toString() ?: "업로드 시작 실패")
        }
        return res.data
    }

    suspend fun startClothingUpload(
        context: Context,
        uri: Uri,
        sectionId: Int
    ): ClothingUploadStartDto {
        val imagePart = MultipartUtil.uriToMultipart(context, uri)
        val sectionPart = MultipartUtil.textPart(sectionId.toString())
        return startClothingUpload(imagePart, sectionPart)
    }

    suspend fun getStatus(jobId: String): ClothingUploadStatusDto {
        val res = api.getClothingUploadStatus(jobId)
        if (!res.success || res.data == null) {
            throw RuntimeException(res.error?.toString() ?: "상태 조회 실패")
        }
        return res.data
    }
}
