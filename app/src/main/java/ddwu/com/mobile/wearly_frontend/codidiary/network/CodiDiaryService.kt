package ddwu.com.mobile.wearly_frontend.codiDiary.network

import CodiDiaryRecordRequest
import CodiDiaryRecordResponse
import ddwu.com.mobile.wearly_frontend.codiDiary.data.CodiDiaryDateListResponse
import ddwu.com.mobile.wearly_frontend.codiDiary.data.CodiDiaryReadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface CodiDiaryService {
    // 일기가 작성된 날짜 조회
    @GET("api/v1/wear-records/dates")
    suspend fun getWearRecordsDates(
        @Header("Authorization") token: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<CodiDiaryDateListResponse>


    // 저장
    @POST("api/v1/wear-records")
    suspend fun postWearRecord(
        @Header("Authorization") token: String,
        @Query("isWeatherLog") isWeatherLog: Boolean,
        @Body request: CodiDiaryRecordRequest
    ): Response<CodiDiaryRecordResponse>


    // 조회
    @GET("api/v1/wear-records")
    suspend fun getWearRecord(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): Response<CodiDiaryReadResponse>
}