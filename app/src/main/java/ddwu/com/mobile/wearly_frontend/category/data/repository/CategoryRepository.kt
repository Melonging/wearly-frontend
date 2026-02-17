package ddwu.com.mobile.wearly_frontend.category.data.repository

import ddwu.com.mobile.wearly_frontend.category.data.ClothingItem
import ddwu.com.mobile.wearly_frontend.category.data.remote.CategoryApi

class CategoryRepository (private val api: CategoryApi) {
    suspend fun getCategories() = api.getCategories()
    suspend fun getAllClothes() = api.getAllClothes()
    suspend fun getClothesByCategory(categoryId: Long) = api.getClothesByCategory(categoryId)
    suspend fun getAllClothesItems(): List<ClothingItem> {
        val res = api.getAllClothes()
        if (!res.success) error(res.error?.message ?: "조회 실패")
        return res.data?.clothes.orEmpty().map { ClothingItem(it.clothing_id, it.image) }
    }

}
