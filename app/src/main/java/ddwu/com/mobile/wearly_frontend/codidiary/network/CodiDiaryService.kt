package ddwu.com.mobile.wearly_frontend.codidiary.network


import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiCategoryResponse
import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiClothesResponse
import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiDiaryDateListResponse
import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiDiaryDeleteResponse
import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiDiaryEditRequest
import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiDiaryEditResponse
import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiDiaryReadResponse
import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiDiaryRecordRequest
import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiDiaryRecordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CodiDiaryService {
    // 일기가 작성된 날짜 조회
    @GET("api/v1/wear-records/dates")
    suspend fun getWearRecordsDates(
        @Header("Authorization") token: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<CodiDiaryDateListResponse>


    // 일기 저장
    @POST("api/v1/wear-records")
    suspend fun postWearRecord(
        @Header("Authorization") token: String,
        @Query("isWeatherLog") isWeatherLog: Boolean,
        @Body request: CodiDiaryRecordRequest
    ): Response<CodiDiaryRecordResponse>


    // 일기 조회
    @GET("api/v1/wear-records")
    suspend fun getWearRecord(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): Response<CodiDiaryReadResponse>

    @GET("api/v1/wear-records")
    suspend fun getWearRecordsByMonth(
        @Header("Authorization") token: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<CodiDiaryReadResponse>



    // 일기 수정
    @PATCH("api/v1/wear-records/{dateId}")
    suspend fun updateDiaryRecord(
        @Header("Authorization") token: String,
        @Path("dateId") dateId: Int,
        @Body request: CodiDiaryEditRequest
    ): Response<CodiDiaryEditResponse>

    // 일기 삭제
    @DELETE("api/v1/wear-records/{dateId}")
    suspend fun deleteWearRecord(
        @Header("Authorization") token: String,
        @Path("dateId") dateId: Int
    ): Response<CodiDiaryDeleteResponse>


    // 옷 카테고리 조회
    @GET("api/outfit/categories")
    suspend fun getCategories(
        @Header("Authorization") token: String
    ): Response<CodiCategoryResponse>

    // 옷 목록 조회
    @GET("api/outfit/categories/{categoryId}/clothes")
    suspend fun getClothesByCategory(
        @Header("Authorization") token: String,
        @Path("categoryId") categoryId: Int
    ): Response<CodiClothesResponse>
}