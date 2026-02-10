package ddwu.com.mobile.wearly_frontend.closet.network

import ddwu.com.mobile.wearly_frontend.closet.data.ApiResponse
import ddwu.com.mobile.wearly_frontend.closet.data.ClosetData
import ddwu.com.mobile.wearly_frontend.closet.data.ClosetItem
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ClosetService {
    //옷장 목록 가져오기
    @GET("/api/v1/closet")
    suspend fun getClosetList( @Header("Authorization") token: String ): ApiResponse<List<ClosetItem>>

    //옷장 조회(섹션이름, 별명, 옷개수)
    @GET("/api/v1/closet/{closetId}")
    suspend fun getClosetDetail( @Header("Authorization") token: String,
                         @Path("closetId") closetId: Int) : ApiResponse<ClosetData>

}