package ddwu.com.mobile.wearly_frontend.upload.data.remote

import ddwu.com.mobile.wearly_frontend.upload.data.entity.ClothingDetail
import ddwu.com.mobile.wearly_frontend.upload.data.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ClosetApi {
    @GET("api/v1/closet/{clothesId}")
    suspend fun getClothesDetail(
        @Path("clothesId") clothesId: Int
    ): ApiResponse<ClothingDetail>
}
