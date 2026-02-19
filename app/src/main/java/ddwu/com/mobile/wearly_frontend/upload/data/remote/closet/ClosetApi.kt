package ddwu.com.mobile.wearly_frontend.upload.data.remote.closet

import ddwu.com.mobile.wearly_frontend.closet.data.CategoryData
import ddwu.com.mobile.wearly_frontend.closet.data.ClosetItem
import ddwu.com.mobile.wearly_frontend.closet.data.ClosetViewData
import ddwu.com.mobile.wearly_frontend.closet.data.ClothingDetail
import ddwu.com.mobile.wearly_frontend.closet.data.DeleteClosetData
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothingDeleteResponseDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothingUpdateRequestDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothingUpdateResponseDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.SectionClothesData
import ddwu.com.mobile.wearly_frontend.closet.data.ApiResponse
import ddwu.com.mobile.wearly_frontend.closet.data.CreateClosetRequest
import ddwu.com.mobile.wearly_frontend.closet.data.UpdateClosetNameRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ClosetApi {
    //홈 화면 옷장 목록 조회
    @GET("api/closet")
    suspend fun getHomeClosetList(): ApiResponse<List<ClosetItem>>

    //옷장 추가 체크
    @POST("api/closet")
    suspend fun setNewCloset(
        @Body body: CreateClosetRequest
    ): ApiResponse<ClosetItem>

    //옷장 뷰 조회
    @GET("api/closet/{closetId}/view")
    suspend fun getClosetView(
        @Path("closetId") closetId: Int
    ): ApiResponse<ClosetViewData>

    //섹션 속 옷 조회
    @GET("api/closet/sections/{sectionId}/clothes")
    suspend fun getSectionClothes(
        @Path("sectionId") sectionId: Int
    ): ApiResponse<SectionClothesData>

    //옷 상세 정보 조회
    @GET("api/closet/clothing/{clothingId}")
    suspend fun getClothingDetail(
        @Path("clothingId") clothingId: Int
    ): ApiResponse<ClothingDetail>

    //옷장 이름 변경
    @PUT("api/closet/{closetId}")
    suspend fun updateClosetName(
        @Path("closetId") closetId: Int,
        @Body request: UpdateClosetNameRequest
    ): ApiResponse<ClosetItem>

    //옷장 삭제
    @DELETE("api/closet/{closetId}")
    suspend fun deleteCloset(
        @Path("closetId") closetId: Int
    ): ApiResponse<DeleteClosetData>

    //카테고리 조회
    @GET("api/outfit/categories")
    suspend fun getCategories(): ApiResponse<CategoryData>


    // 옷 정보 수정
    @PUT("api/upload/clothing/{clothingId}")
    suspend fun updateClothing(
        @Path("clothingId") clothingId: Int,
        @Body body: ClothingUpdateRequestDto
    ): ClothingUpdateResponseDto

    // 옷 삭제
    @DELETE("api/upload/clothing/{clothingId}")
    suspend fun deleteClothing(
        @Path("clothingId") clothingId: Int
    ): ClothingDeleteResponseDto
}
