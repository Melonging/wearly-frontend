package ddwu.com.mobile.wearly_frontend.records.data.remote

import ddwu.com.mobile.wearly_frontend.records.data.dto.UpdateWearRecordRequest
import ddwu.com.mobile.wearly_frontend.records.data.dto.UpdateWearRecordResponse
import ddwu.com.mobile.wearly_frontend.records.data.dto.WearRecordResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface RecordsApi {
    @GET("api/v1/wear-records")
    suspend fun getWearRecords(
        @Query("year") year: Int? = null,
        @Query("month") month: Int? = null,
        @Query("date") date: String? = null,
        @Query("is_heart") isHeart: String? = null
    ): WearRecordResponse

    @PATCH("api/v1/wear-records/{dateId}")
    suspend fun updateWearRecord(
        @Path("dateId") dateId: Long,
        @Body body: UpdateWearRecordRequest
    ): UpdateWearRecordResponse
}
