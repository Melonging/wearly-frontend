package ddwu.com.mobile.wearly_frontend.upload.data.remote.closet

import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.CategoryResponse
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClosetListResponseDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothesDetailDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothesDetailInnerDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothingDeleteResponseDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothingUpdateRequestDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothingUpdateResponseDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.SectionClothesData
import ddwu.com.mobile.wearly_frontend.upload.data.remote.common.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
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
    ): ApiResponse<ClothesDetailDto>


    // 카테고리 조회
    @GET("/api/outfit/categories")
    suspend fun getCategories(): CategoryResponse

    // 옷장 목록 조회
    @GET("/api/closet")
    suspend fun getClosets(): ClosetListResponseDto

    // 옷 정보 수정
    @PATCH("/api/upload/clothing/{clothingId}")
    suspend fun updateClothing(
        @Path("clothingId") clothingId: Long,
        @Body body: ClothingUpdateRequestDto
    ): ClothingUpdateResponseDto

    // 옷 삭제
    @DELETE("/api/upload/clothing/{clothingId}")
    suspend fun deleteClothing(
        @Path("clothingId") clothingId: Long
    ): ClothingDeleteResponseDto

}
