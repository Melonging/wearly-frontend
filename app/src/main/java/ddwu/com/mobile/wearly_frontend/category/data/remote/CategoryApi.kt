package ddwu.com.mobile.wearly_frontend.category.data.remote

import ddwu.com.mobile.wearly_frontend.category.data.dto.CategoriesResponse
import ddwu.com.mobile.wearly_frontend.category.data.dto.ClothesResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoryApi {

    @GET("api/outfit/categories")
    suspend fun getCategories(): CategoriesResponse

    @GET("api/outfit/categories/{categoryId}/clothes")
    suspend fun getClothesByCategory(
        @Path("categoryId") categoryId: Long
    ): ClothesResponse

    @GET("api/outfit/clothes")
    suspend fun getAllClothes(): ClothesResponse
}