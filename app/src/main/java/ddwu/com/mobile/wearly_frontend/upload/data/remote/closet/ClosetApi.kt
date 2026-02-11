package ddwu.com.mobile.wearly_frontend.upload.data.remote.closet

import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothesDetailInnerDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.SectionClothesData
import ddwu.com.mobile.wearly_frontend.upload.data.remote.common.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ClosetApi {

    // 섹션 조회
    @GET("api/closet/sections/{sectionId}/clothes")
    suspend fun getSectionClothes(
        @Path("sectionId") sectionId: Int
    ): ApiResponse<SectionClothesData>

    // 옷 조회

    @GET("api/closet/clothing/{clothingId}")
    suspend fun getClothesDetail(
        @Path("clothingId") clothingId: Long
    ): ApiResponse<ClothesDetailInnerDto>
}
