package ddwu.com.mobile.wearly_frontend.codiDiary.network

import ddwu.com.mobile.wearly_frontend.codiDiary.data.CodiDiaryRecordRequest
import ddwu.com.mobile.wearly_frontend.codiDiary.data.CodiDiaryRecordResponse
import ddwu.com.mobile.wearly_frontend.codiDiary.data.CodiDiaryViewResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface CodiDiaryService {
    // 일기 저장 (추가)
    @POST("api/v1/wear-records")
    suspend fun saveWearRecord(
        @Query("isWeatherLog") isWeatherLog: Boolean = true,
        @Body record: CodiDiaryRecordRequest
    ): CodiDiaryRecordResponse

    // 일기 조회
    @GET("api/v1/wear-records")
    suspend fun getWearRecords(
        @Header("Authorization") token: String,
        @Query("year") year: Int? = null,
        @Query("month") month: Int? = null,
        @Query("date") date: String? = null, // YYYY-MM-DD
        @Query("is_heart") isHeart: Boolean? = null
    ): CodiDiaryViewResponse
}