package ddwu.com.mobile.wearly_frontend.category.network

import ddwu.com.mobile.wearly_frontend.closet.data.ApiResponse
import ddwu.com.mobile.wearly_frontend.category.data.CategoryData
import retrofit2.http.GET

interface CodiPlanService {
    @GET("/api/outfit/categories")
    suspend fun getCategories(): ApiResponse<CategoryData>
}
