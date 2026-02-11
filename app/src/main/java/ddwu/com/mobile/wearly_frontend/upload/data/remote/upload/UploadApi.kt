package ddwu.com.mobile.wearly_frontend.upload.data.remote.upload

import ddwu.com.mobile.wearly_frontend.upload.data.model.upload.ClothingUploadStartDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.upload.ClothingUploadStatusDto
import ddwu.com.mobile.wearly_frontend.upload.data.remote.common.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
interface UploadApi {
    @Multipart
    @POST("api/upload/clothing-async")
    suspend fun startClothingUpload(
        @Part image: MultipartBody.Part,
        @Part("sectionId") sectionId: RequestBody
    ): ApiResponse<ClothingUploadStartDto>

    @GET("api/upload/clothing-status/{jobId}")
    suspend fun getClothingUploadStatus(
        @Path("jobId") jobId: String
    ): ApiResponse<ClothingUploadStatusDto>
}
