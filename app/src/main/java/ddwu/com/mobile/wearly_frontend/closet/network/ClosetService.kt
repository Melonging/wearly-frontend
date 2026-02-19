package ddwu.com.mobile.wearly_frontend.closet.network

import ddwu.com.mobile.wearly_frontend.closet.data.ApiResponse
import ddwu.com.mobile.wearly_frontend.closet.data.ClosetItem
import ddwu.com.mobile.wearly_frontend.closet.data.ClosetViewData
import ddwu.com.mobile.wearly_frontend.closet.data.ClothingDetail
import ddwu.com.mobile.wearly_frontend.closet.data.DeleteClosetData
import ddwu.com.mobile.wearly_frontend.closet.data.SectionClothesData
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ClosetService {
    //홈 화면 옷장 목록 조회
    @GET("api/closet")
    suspend fun getHomeClosetList(
        @Header("Authorization") token: String
    ): ApiResponse<List<ClosetItem>>

    //옷장 추가 체크
    @POST("api/closet")
    suspend fun setNewCloset(
        @Header("Authorization") token: String,
        @Query("closetName") closetName: String
    ): ApiResponse<ClosetItem>

    //옷장 뷰 조회
    @GET("api/closet/{closetId}/view")
    suspend fun getClosetView(
        @Header("Authorization") token: String,
        @Path("closetId") closetId: Int
    ): ApiResponse<ClosetViewData>

    //섹션 속 옷 조회
    @GET("api/closet/sections/{sectionId}/clothes")
    suspend fun getSectionClothes(
        @Header("Authorization") token: String,
        @Path("sectionId") sectionId: Int
    ): ApiResponse<SectionClothesData>

    //옷 상세 정보 조회
    @GET("api/closet/clothing/{clothingId}")
    suspend fun getClothingDetail(
        @Header("Authorization") token: String,
        @Path("clothingId") clothingId : Int
    ): ApiResponse<ClothingDetail>

    //옷장 이름 변경
    @PUT("api/closet/{closetId}")
    suspend fun changeClosetName(
        @Header("Authorization") token: String,
        @Path("closetId") closetId : Int
    ): ApiResponse<ClosetItem>

    //옷장 삭제
    @DELETE("api/closet/{closetId}")
    suspend fun deleteCloset(
        @Header("Authorization") token: String,
        @Path("closetId") closetId : Int
    ): ApiResponse<DeleteClosetData>
}