package ddwu.com.mobile.wearly_frontend.upload.data.remote

import ddwu.com.mobile.wearly_frontend.upload.data.model.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

data class UploadStartData(val jobId: String, val status: String)

data class ClothingStatusData(
    val jobId: String,
    val status: String,
    val result: ClothingResult? = null,
    val error: String? = null
)

data class ClothingResult(
    val clothing: ClothingInfo?
)

data class ClothingInfo(
    val clothing_id: Int,
    val image: String,
    val category: String?,
    val section_name: String?,
    val temperature: Int?
)

interface UploadApi {
    @Multipart
    @POST("api/upload/clothing")
    suspend fun startClothingUpload(
        @Part image: MultipartBody.Part,
        @Part("sectionId") sectionId: RequestBody
    ): ApiResponse<UploadStartData>

    @GET("api/upload/clothing-status/{jobId}")
    suspend fun getClothingUploadStatus(
        @Path("jobId") jobId: String
    ): ApiResponse<ClothingStatusData>
}
