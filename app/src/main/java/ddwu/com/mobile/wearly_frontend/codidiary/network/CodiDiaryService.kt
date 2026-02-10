package ddwu.com.mobile.wearly_frontend.codidiary.network

import ddwu.com.mobile.wearly_frontend.codidiary.data.CategoryResponse
import ddwu.com.mobile.wearly_frontend.codidiary.data.ClothesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CodiDiaryService {
    // 1. 카테고리 목록 전체 가져오기
    @GET("/api/outfit/categories")
    fun getCategories(): Call<CategoryResponse>

    @GET("/api/outfit/categories/{categoryId}/clothes")
    fun getClothesByCategory(
        @Path("categoryId") categoryId: Int
    ): Call<ClothesResponse>
}